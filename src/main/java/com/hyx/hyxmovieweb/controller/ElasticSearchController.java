package com.hyx.hyxmovieweb.controller;

import com.hyx.hyxmovieweb.service.ElasticSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/movie-indices")
public class ElasticSearchController {

    private final ElasticSearchService elasticSearchService;

    public ElasticSearchController(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @PostMapping("/indices")
    public ResponseEntity<String> createIndex() {
        elasticSearchService.createIndexWithMapping();

        return ResponseEntity.ok("Index and mapping created");
    }

    @PostMapping("/free-search")
    public ResponseEntity<Object> searchFree(@RequestBody Map<String, Object> condition) {
        return ResponseEntity.ok(elasticSearchService.freeSearch(condition));
    }

    @PostMapping("/data-sync")
    public ResponseEntity<String> syncAll() {
        elasticSearchService.syncAllToEs();
        return ResponseEntity.ok("Sync success");
    }

    @GetMapping("/classify-groups")
    public ResponseEntity<Map<String, Long>> getClassifyGroups() {
        return ResponseEntity.ok(elasticSearchService.countByClassify());
    }
}