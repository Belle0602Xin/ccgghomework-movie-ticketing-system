package com.hyx.hyxmovieweb.repository;

import com.hyx.hyxmovieweb.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film, Integer> {
}