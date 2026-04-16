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
    private final MongoTemplate mongoTemplate;

    public MongoDBService(OrderRepository orderRepository, MovieRepository movieRepository, FilmRepository filmRepository, MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.movieRepository = movieRepository;
        this.filmRepository = filmRepository;
        this.mongoTemplate = mongoTemplate;
    }

    private MongoDBOrder convertToMongoOrder(Order mo) {
        MongoDBOrder mongoDBOrder = new MongoDBOrder();
        mongoDBOrder.setOrderTime(mo.orderTime);
        mongoDBOrder.setPrice(mo.totalPrice);
        mongoDBOrder.setQuantity(mo.ticketsQuality);
        mongoDBOrder.setCustomerId(mo.customerId);

        movieRepository.findById(mo.scheduleId)
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

        List<MongoDBOrder> mongoOrders = allOrders.stream().map(mo -> {
            MongoDBOrder mongoDBOrder = new MongoDBOrder();
            mongoDBOrder.setTicketNo(String.valueOf(System.currentTimeMillis()));
            mongoDBOrder.setOrderTime(mo.getOrderTime());
            mongoDBOrder.setQuantity(mo.getTicketsQuality());
            mongoDBOrder.setPrice(mo.getTotalPrice());
            mongoDBOrder.setAddress("Secaucus Cinema No." + mo.getScheduleId());

            Movie movie = movieMap.get(mo.getScheduleId());
            if (movie != null) {
                Film f = filmMap.get(movie.getFilmId());
                if (f != null) {
                    mongoDBOrder.setFilmName(f.getName());
                    mongoDBOrder.setClassify(f.getClassify());
                }
            }
            return mongoDBOrder;
        }).toList();

        mongoTemplate.dropCollection("orders");
        mongoTemplate.insertAll(mongoOrders);
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
}