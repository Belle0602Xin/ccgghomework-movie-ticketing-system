package com.hyx.hyxmovieweb.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "t_schedule")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(name = "price")
    public Double moviePrice;

    @Column(name = "quota")
    public Integer ticketsAvailable;

    @Column(name = "show_time")
    public String movieTime;

    @Column(name = "f_id")
    public Integer filmId;

    @Version
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getMovieName() {
        return "电影编号: " + filmId;
    }

    public Integer getTicketsAvailable() {
        return ticketsAvailable;
    }

    public void setTicketsAvailable(Integer quota) {
        this.ticketsAvailable = quota;
    }

    public Double getMoviePrice() {
        return moviePrice.doubleValue();
    }

    public Integer getFilmId() {
        return this.filmId;
    }
}