package com.hyx.hyxmovieweb.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.hyx.hyxmovieweb.entity.FilmElasticSearch;
import com.hyx.hyxmovieweb.entity.Film;
import com.hyx.hyxmovieweb.repository.FilmRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    private final FilmRepository filmRepository;

    private final JdbcTemplate jdbcTemplate;

    public ElasticSearchService(ElasticsearchOperations elasticsearchOperations, FilmRepository filmRepository, JdbcTemplate jdbcTemplate) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.filmRepository = filmRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createIndexWithMapping() {
        elasticsearchOperations.indexOps(FilmElasticSearch.class).createWithMapping();
    }

    public void syncAllToEs() {
        List<Film> films = filmRepository.findAll();

        List<FilmElasticSearch> filmESList = films.stream().map(film -> {
            FilmElasticSearch filmElasticSearch = new FilmElasticSearch();
            filmElasticSearch.setId(film.getId());
            filmElasticSearch.setName(film.getName());
            filmElasticSearch.setClassify(film.getClassify());
            filmElasticSearch.setDirector(film.getDirector());
            filmElasticSearch.setHero(film.getHero());
            filmElasticSearch.setHeroine(film.getHeroine() == null ? "Unknown" : film.getHeroine());
            filmElasticSearch.setProduction(film.getProduction() != null ? film.getProduction().toString() : "Unknown Year");

            try {
                String outline = jdbcTemplate.queryForObject(
                        "SELECT outline FROM t_outline WHERE id = ?",
                        String.class, film.getId()
                );
                filmElasticSearch.setOutline(outline);
            } catch (Exception e) {
                filmElasticSearch.setOutline("No outline available");
            }
            return filmElasticSearch;
        }).collect(Collectors.toList());

        elasticsearchOperations.save(filmESList);
    }


    public Object freeSearch(Map<String, Object> condition) {
        String content = (String) condition.get("content");
        if (content == null || content.isEmpty()) {
            throw new RuntimeException("Search content cannot be empty");
        }

        int pageNo = (int) condition.getOrDefault("pageNo", 1);
        int pageSize = (int) condition.getOrDefault("pageSize", 10);

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .fields("name", "director", "hero", "heroine", "outline")
                        .query(content)
                ))

                .withPageable(PageRequest.of(pageNo, pageSize))
                .build();

        return performSearch(content, pageNo - 1, pageSize);
    }

    private SearchPage<FilmElasticSearch> performSearch(String content, int pageNo, int pageSize) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m.fields("name", "director").query(content)))
                .withPageable(PageRequest.of(pageNo, pageSize))
                .build();
        return SearchHitSupport.searchPageFor(elasticsearchOperations.search(query, FilmElasticSearch.class), query.getPageable());
    }

    public Map<String, Long> countByClassify() {
        NativeQuery query = NativeQuery.builder()
                .withAggregation("by_classify", Aggregation.of(a -> a.terms(t -> t.field("classify"))))
                .withMaxResults(0)
                .build();

        SearchHits<FilmElasticSearch> searchHits = elasticsearchOperations.search(query, FilmElasticSearch.class);

        Map<String, Long> result = new HashMap<>();

        if (searchHits.getAggregations() != null) {
            ElasticsearchAggregations elasticsearchAggregations = (ElasticsearchAggregations) searchHits.getAggregations();
            elasticsearchAggregations.aggregationsAsMap().get("by_classify").aggregation()
                    .getAggregate().sterms().buckets().array().forEach(bucket -> {
                        result.put(bucket.key().stringValue(), bucket.docCount());
                    });
        }
        return result;
    }
}


//    public Map<String, Long> countByClassify() {
//        return new HashMap<>();
//    }


//public List<FilmElasticSearch> searchByCondition(Map<String, Object> condition) {
//    NativeQuery query = NativeQuery.builder()
//            .withQuery(q -> q.bool(b -> {
//                if (condition.get("name") != null) {
//                    b.must(m -> m.match(t -> t.field("name").query(condition.get("name").toString())));
//                }
//
//                if (condition.get("classify") != null) {
//                    b.must(m -> m.match(t -> t.field("classify").query(condition.get("classify").toString())));
//                }
//
//                if (condition.get("year") != null) {
//                    b.must(m -> m.match(t -> t.field("production").query(condition.get("year").toString())));
//                }
//
//                return b;
//            }))
//            .withMaxResults(10)
//            .build();
//
//    return elasticsearchOperations.search(query, FilmElasticSearch.class)
//            .getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());

//SearchHits<FilmElasticSearch> searchHits = elasticsearchOperations.search(query, FilmElasticSearch.class);