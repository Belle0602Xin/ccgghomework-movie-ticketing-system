package com.hyx.hyxmovieweb.repository;

import com.hyx.hyxmovieweb.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByFilmId(Integer filmId);
}