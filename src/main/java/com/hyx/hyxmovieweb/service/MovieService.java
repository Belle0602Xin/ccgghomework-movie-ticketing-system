package com.hyx.hyxmovieweb.service;

import com.hyx.hyxmovieweb.entity.*;
import com.hyx.hyxmovieweb.repository.*;
import jakarta.transaction.Transactional;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final FilmRepository filmRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public MovieService(MovieRepository movieRepository, UserRepository userRepository, OrderRepository orderRepository, FilmRepository filmRepository, RedissonClient redissonClient, RedisTemplate<String, Object> redisTemplate, RabbitTemplate rabbitTemplate) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.filmRepository = filmRepository;
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    private String encodePassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(encodePassword(password))) {
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("login:token:" + token, user, 30, TimeUnit.MINUTES);

            return token;
        }

        return null;
    }

    public void addUser(User user) {
        user.setPassword(encodePassword(user.getPassword()));

        if (user.getSalt() == null || user.getSalt().isEmpty()) {
            user.setSalt("default_salt");
        }

        userRepository.save(user);
    }

    public Page<Movie> getMoviesPage(int page) {
        Page<Movie> moviePage = movieRepository.findAll(PageRequest.of(page, 5));

        for (Movie movie : moviePage.getContent()) {
            if (movie.getFilmId() != null) {
                filmRepository.findById(movie.getFilmId()).ifPresent(film -> {
                    movie.setMovieName(film.getName());
                });
            }
        }
        return moviePage;
    }

    public List<Order> getSalesStatistics() {
        List<Order> allOrders = orderRepository.findAll();
        for (Order order : allOrders) {
            movieRepository.findById(order.scheduleId)
                    .flatMap(schedule -> filmRepository.findById(schedule.getFilmId()))
                    .ifPresent(film -> order.movieName = film.getName());

            if (order.movieName == null) {
                order.movieName = "Session ID: " + order.scheduleId;
            }
        }
        return allOrders;
    }

    public List<Order> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return List.of();
        }

        List<Order> orders = orderRepository.findByCustomerId(user.getId());
        for (Order order : getOrders(orders)) {
            movieRepository.findById(order.getScheduleId())
                    .flatMap(movie -> filmRepository.findById(movie.getFilmId()))
                    .ifPresent(film -> order.setMovieName(film.getName()));

            if (order.movieName == null) {
                order.movieName = "Session ID: " + order.scheduleId;
            }
        }
        return orders;
    }

    private static List<Order> getOrders(List<Order> orders) {
        return orders;
    }

    @Transactional
    public String bookTicket(int mid, int count, String uid) {
        User user = userRepository.findByUsername(uid);
        if (user == null) {
            throw new RuntimeException("User does not exist.");
        }
        Integer currentUserId = user.getId();

        String lockKey = "lock:movie:" + mid;
        String stockKey = "ticket:stock:" + mid;

        RLock rlock = redissonClient.getLock(lockKey);

        try {
            if (rlock.tryLock(3, 10, TimeUnit.SECONDS)) {
                Object stockObj = redisTemplate.opsForHash().get("movie:stocks", String.valueOf(mid));
                int stock = (stockObj != null) ? Integer.parseInt(stockObj.toString()) : 0;

                if (stock >= count) {
                    redisTemplate.opsForValue().decrement(stockKey, count);

                    Movie movie = movieRepository.findById(mid).orElseThrow(() -> new RuntimeException("场次不存在"));

                    movie.setTicketsQuota(movie.getTicketsQuota() - count);
                    movieRepository.save(movie);

                    Order order = new Order();
                    order.setCustomerId(currentUserId);
                    order.orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    order.scheduleId = mid;
                    order.ticketsQuality = count;
                    order.totalPrice = count * movie.getMoviePrice();

                    user = userRepository.findByUsername(uid);
                    if (user != null) {
                        order.customerId = user.getId();
                    }

                    orderRepository.save(order);

                    rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                        if (ack) {
                            System.out.println("Message successfully reached the Exchange");
                        } else {
                            System.err.println("Message failed to send: " + cause);
                        }

                    });

                    rabbitTemplate.convertAndSend("exchange.order", "newOrder", order);

                    System.out.println("Order message pushed to RabbitMQ queue, Order ID: " + order.id);

                    return "Success: User: " + uid + "(CustomerID: " + currentUserId + ") successfully got the ticket.";
                } else {
                    return "Fail: Sold out!";
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            return "Fail: System Error";
        } finally {
            if (rlock.isHeldByCurrentThread()) {
                rlock.unlock();
            }
        }
        return "Fail: System busy, try again.";
    }

    public void warmUpRedisInventory() {
        List<Movie> movies = movieRepository.findAll();

        Map<String, String> inventoryMap = new HashMap<>();
        for (Movie movie : movies) {
            inventoryMap.put(movie.getId().toString(), movie.getTicketsQuota().toString());
        }

        redisTemplate.opsForHash().putAll("movie:stocks", inventoryMap);
        System.out.println("Redis Warm-up: All movie stocks loaded into Hash structure.");
    }
}

//private static double getMoviePrice(Movie movie) {
//    return movie.getMoviePrice();
//}
//public Movie findMovieById(int mid) {
//    return movieRepository.findById(mid).orElse(null);
//}
//
//public List<Order> getOrders() {
//    return orderRepository.findAll();
//}