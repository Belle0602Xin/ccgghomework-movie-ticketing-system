package com.hyx.hyxmovieweb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import lombok.Data;

@Data
@Document(indexName = "movies")
public class FilmElasticSearch {
    @Id
    private Integer id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    @Field(type = FieldType.Keyword)
    private String classify;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String director;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String hero;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String heroine;

    @Field(type = FieldType.Text)
    private String production;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String outline;
}