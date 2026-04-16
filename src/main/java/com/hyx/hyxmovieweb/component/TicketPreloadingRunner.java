package com.hyx.hyxmovieweb.component;

import com.hyx.hyxmovieweb.repository.FilmRepository;
import com.mongodb.lang.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Component
@Order(1)
public class TicketPreloadingRunner implements CommandLineRunner {

    private final RedisTemplate<String, Object> redisTemplate;

    private final FilmRepository filmRepository;

    public TicketPreloadingRunner(RedisTemplate<String, Object> redisTemplate, FilmRepository filmRepository) {
        this.redisTemplate = redisTemplate;
        this.filmRepository = filmRepository;
    }

    @Override
    public void run(@NonNull String... args) {
        filmRepository.findAll().forEach(film -> {
            redisTemplate.opsForHash().put("movie:stocks", String.valueOf(film.getId()), 100);
        });

        System.out.println("Redis Warm-up: All movie ticket stocks loaded.");
    }
}
