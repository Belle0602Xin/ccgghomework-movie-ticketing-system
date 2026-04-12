package com.hyx.hyxmovieweb.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "t_film")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "classify")
    private String classify;

    @Column(name = "director")
    private String director;

    @Column(name = "hero")
    private String hero;

    @Column(name = "heroine")
    private String heroine;

    @Column(name = "production")
    private java.sql.Date production;

    @Transient
    private String outline;
}