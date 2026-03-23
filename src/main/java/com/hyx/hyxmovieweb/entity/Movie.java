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

    @Transient
    private String movieName;

    public Integer getVersion() {
        return version;
    }

    public String getMovieName() {
        return this.movieName;
    }

    public Integer getTicketsAvailable() {
        return ticketsAvailable;
    }

    public Double getMoviePrice() {
        return moviePrice.doubleValue();
    }

    public Integer getFilmId() {
        return this.filmId;
    }

    public void setTicketsAvailable(Integer quota) {
        this.ticketsAvailable = quota;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}