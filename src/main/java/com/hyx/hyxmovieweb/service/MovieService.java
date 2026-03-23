package com.hyx.hyxmovieweb.service;

import com.hyx.hyxmovieweb.entity.*;
import com.hyx.hyxmovieweb.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class MovieService {

    @Autowired private MovieRepository movieRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private OrderRepository orderRepo;
    @Autowired private FilmRepository filmRepo;

    private String encodePassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    public User login(String username, String password) {
        User user = userRepo.findByUsername(username);

        if (user != null && user.getPassword().equals(encodePassword(password))) {
            return user;
        }

        return null;
    }

    public void addUser(User user) {
        user.setPassword(encodePassword(user.getPassword()));

        if (user.getSalt() == null || user.getSalt().isEmpty()) {
            user.setSalt("default_salt");
        }

        userRepo.save(user);
    }

    public Page<Movie> getMoviesPage(int page) {
        return movieRepo.findAll(PageRequest.of(page, 5));
    }

    @Transactional
    public String bookTicket(int mid, int count, String uid) {
        Movie movie = movieRepo.findById(mid).orElseThrow(() -> new RuntimeException("场次不存在"));

        if (movie.getTicketsAvailable() < count) {
            throw new RuntimeException("余票不足");
        }

        movie.setTicketsAvailable(movie.getTicketsAvailable() - count);
        movieRepo.save(movie);

        Order order = new Order();
        order.orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        order.sessionId = mid;
        order.ticketsCount = count;
        order.totalAmount = count * movie.getMoviePrice();

        User user = userRepo.findByUsername(uid);

        if (user != null) {
            order.customerId = user.getId();
        }

        orderRepo.save(order);

        return "SUCCESS";
    }

    public Movie findMovieById(int mid) {
        return movieRepo.findById(mid).orElse(null);
    }

    public List<Order> getOrders() {
        return orderRepo.findAll();
    }

    public List<Order> getSalesStatistics() {
        List<Order> allOrders = orderRepo.findAll();
        for (Order order : allOrders) {
            movieRepo.findById(order.sessionId).ifPresent(schedule -> {
                filmRepo.findById(schedule.getFilmId()).ifPresent(film -> {
                    order.movieName = film.getName();
                });
            });

            if (order.movieName == null) {
                order.movieName = "场次: " + order.sessionId;
            }
        }
        return allOrders;
    }

    public List<Order> getOrdersByUsername(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            return List.of();
        }

        List<Order> orders = orderRepo.findByCustomerId(user.getId());

        for (Order order : orders) {
            movieRepo.findById(order.sessionId).ifPresent(schedule -> {
                filmRepo.findById(schedule.getFilmId()).ifPresent(film -> {
                    order.movieName = film.getName();
                });
            });

            if (order.movieName == null) {
                order.movieName = "场次: " + order.sessionId;
            }
        }
        return orders;
    }
}