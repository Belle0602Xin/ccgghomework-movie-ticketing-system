package com.hyx.hyxmovieweb.service;

import com.hyx.hyxmovieweb.entity.*;
import com.hyx.hyxmovieweb.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class MongoDBService {

    private final OrderRepository orderRepository;
    private final MovieRepository movieRepository;
    private final FilmRepository filmRepository;
    private final TheaterRepository theaterRepository;
    private final MongoTemplate mongoTemplate;

    public MongoDBService(OrderRepository orderRepository, MovieRepository movieRepository, FilmRepository filmRepository, TheaterRepository theaterRepository, MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.movieRepository = movieRepository;
        this.filmRepository = filmRepository;
        this.theaterRepository = theaterRepository;
        this.mongoTemplate = mongoTemplate;
    }

    private MongoDBOrder convertToMongoOrder(Order order) {
        MongoDBOrder mongoDBOrder = new MongoDBOrder();
        mongoDBOrder.setOrderTime(order.orderTime);
        mongoDBOrder.setPrice(order.totalPrice);
        mongoDBOrder.setQuantity(order.ticketsQuality);
        mongoDBOrder.setCustomerId(order.customerId);
        mongoDBOrder.setId(order.getId());

        movieRepository.findById(order.scheduleId)
                .flatMap(movie -> filmRepository.findById(movie.getFilmId()))
                .ifPresent(film -> {
                    mongoDBOrder.setFilmName(film.getName());
                    mongoDBOrder.setClassify(film.getClassify());
                });

        return mongoDBOrder;
    }

    public void transferAllOrders() {
        List<Order> allOrders = orderRepository.findAll();

        Map<Integer, Movie> movieMap = movieRepository.findAll().stream()
                .collect(Collectors.toMap(Movie::getId, m -> m));

        Map<Integer, Film> filmMap = filmRepository.findAll().stream()
                .collect(Collectors.toMap(Film::getId, f -> f));

        List<MongoDBOrder> mongoDBOrders = allOrders.stream().map(order -> {
            MongoDBOrder mongoDBOrder = new MongoDBOrder();
            mongoDBOrder.setTicketNo(String.valueOf(System.currentTimeMillis()));
            mongoDBOrder.setOrderTime(order.getOrderTime());
            mongoDBOrder.setQuantity(order.getTicketsQuality());
            mongoDBOrder.setPrice(order.getTotalPrice());
            mongoDBOrder.setAddress("Cinema" + order.getScheduleId());

            Movie movie = movieMap.get(order.getScheduleId());
            if (movie != null) {
                Film film = filmMap.get(movie.getFilmId());
                if (film != null) {
                    mongoDBOrder.setFilmName(film.getName());
                    mongoDBOrder.setClassify(film.getClassify());
                }
            }
            return mongoDBOrder;
        }).toList();

        mongoTemplate.dropCollection("orders");
        mongoTemplate.insertAll(mongoDBOrders);
    }

    public void transferToMyOrders() {
        List<Order> allOrders = orderRepository.findAll();
        mongoTemplate.dropCollection("myOrders");
        for (Order order : allOrders) {
            mongoTemplate.save(convertToMongoOrder(order), "myOrders");
        }
    }

    public List<MongoDBOrder> getMyOrdersPaged(Integer userId, int pageNo, int pageSize) {
        Integer finalId = (userId != null) ? userId : 79;
        Query query = new Query(Criteria.where("customerId").is(finalId));
        query.with(Sort.by(Sort.Direction.DESC, "orderTime"));
        query.skip((long) (pageNo - 1) * pageSize).limit(pageSize);

        return mongoTemplate.find(query, MongoDBOrder.class, "orders");
    }

    public List<Map<String, Object>> getTop3Classify() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("classify")
                        .sum("price").as("totalAmount")
                        .count().as("totalSales"),

                Aggregation.project("totalAmount", "totalSales").and("_id").as("classify"),
                Aggregation.sort(Sort.Direction.DESC, "totalAmount"),
                Aggregation.limit(3)
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "orders", Document.class);
        return results.getMappedResults().stream()
                .map(doc -> (Map<String, Object>) new HashMap<>(doc))
                .toList();
    }

    public void saveSingleOrder(Order order) {
        MongoDBOrder mongoDBOrder = convertToMongoOrder(order);

        movieRepository.findById(order.getScheduleId()).ifPresent(movie -> {
            theaterRepository.findById(movie.getTheaterId()).ifPresent(theater -> {
                mongoDBOrder.setAddress(theater.getAddress());
            });
        });

        if (mongoDBOrder.getAddress() == null) {
            mongoDBOrder.setAddress("Default Cinema Address");
        }

        mongoDBOrder.setTicketNo("T" + System.currentTimeMillis());

        mongoTemplate.save(mongoDBOrder, "orders");
        mongoTemplate.save(mongoDBOrder, "myOrders");

        System.out.println("Async storage successful: Order ID \" + order.id + \" synchronized to MongoDB dual collections.");
    }
}