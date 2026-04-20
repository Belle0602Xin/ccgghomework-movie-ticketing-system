package com.hyx.hyxmovieweb.controller;

import com.hyx.hyxmovieweb.entity.MongoDBOrder;
import com.hyx.hyxmovieweb.service.MongoDBService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mongodb-test")
public class MongoDBController {

    private final MongoDBService mongoDBService;

    public MongoDBController(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @PostMapping("/all-orders-transfer")
    public ResponseEntity<Map<String, Object>> transferOrders() {
        mongoDBService.transferAllOrders();
        return ResponseEntity.ok(Map.of("resultCode", 0, "message", "MySQL成功导入MongoDB"));
    }

    @PostMapping("/my-orders-transfer")
    public ResponseEntity<Map<String, Object>> transferCustom() {
        mongoDBService.transferToMyOrders();
        return ResponseEntity.ok(Map.of("resultCode", 0, "message", "个人订单导入成功"));
    }

    @PostMapping("/my-orders")
    public ResponseEntity<Map<String, Object>> getMyOrders(@RequestParam(defaultValue = "1") Integer pageNo,
                                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                                           HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        List<MongoDBOrder> orders = mongoDBService.getMyOrdersPaged(userId, pageNo, pageSize);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultCode", 0);
        response.put("message", "Success");
        response.put("data", orders);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-classify-statistics")
    public ResponseEntity<Map<String, Object>> top3ByClassify() {
        List<Map<String, Object>> statistics = mongoDBService.getTop3Classify();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultCode", 0);
        response.put("message", "Success");
        response.put("data", statistics);

        return ResponseEntity.ok(response);
    }
}