package com.hyx.hyxmovieweb.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "t_schedule")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "price")
    private Double moviePrice;

    @Column(name = "quota")
    private Integer ticketsQuota;

    @Column(name = "show_time")
    private String showTime;

    @Column(name = "f_id")
    private Integer filmId;

    @Version
    private Integer version;

    @Transient
    private String movieName;
}