package com.hyx.hyxmovieweb.component;

import com.hyx.hyxmovieweb.entity.Order;
import com.hyx.hyxmovieweb.service.MongoDBService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {

    private final MongoDBService mongoDBService;

    public OrderMessageListener(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @RabbitListener(queues = "queue.mongodb")
    public void onMongodbMessage(Order order) {
        System.out.println("Received MongoDB sync task: Order ID: " + order.id);
        mongoDBService.saveSingleOrder(order);
    }

    @RabbitListener(queues = "queue.statistics")
    public void onStatisticsMessage(Order order) {
        System.out.println("Received session statistics task for Order ID: " + order.id);
        // statisticsService.update(order);
    }
}