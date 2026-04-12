package com.hyx.hyxmovieweb.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "t_order")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "order_time")
    public String orderTime;

    @Column(name = "price")
    public Double totalPrice;

    @Column(name = "quality")
    public Integer ticketsQuality;

    @Column(name = "customer_id")
    public Integer customerId;

    @Column(name = "schedule_id")
    public Integer scheduleId;

    @Transient
    public String movieName;
}