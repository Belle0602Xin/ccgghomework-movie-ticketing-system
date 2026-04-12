package com.hyx.hyxmovieweb.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "t_customer")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account", unique = true)
    public String username;

    @Column(name = "password")
    public String password;

    @Column(name = "alias")
    public String alias;

    public String gender;
    public String email;

    @Column(name = "salt")
    private String salt = "";
}