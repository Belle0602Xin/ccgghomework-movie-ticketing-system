package com.hyx.hyxmovieweb.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.hyx.hyxmovieweb.entity.FilmElasticSearch;
import com.hyx.hyxmovieweb.entity.Film;
import com.hyx.hyxmovieweb.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ElasticsearchOperations esOps;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createIndexWithMapping() {
        esOps.indexOps(FilmElasticSearch.class).createWithMapping();
    }

    public void syncAllToEs() {
        List<Film> films = filmRepository.findAll();
        List<FilmElasticSearch> esList = films.stream().map(f -> {
            FilmElasticSearch es = new FilmElasticSearch();
            es.setId(f.getId());
            es.setName(f.getName());
            es.setClassify(f.getClassify());
            es.setDirector(f.getDirector());
            es.setHero(f.getHero());
            es.setHeroine(f.getHeroine() == null ? "未知" : f.getHeroine());
            es.setProduction(f.getProduction() != null ? f.getProduction().toString() : "未知年份");

            try {
                String outline = jdbcTemplate.queryForObject(
                        "SELECT outline FROM t_outline WHERE id = ?",
                        String.class, f.getId()
                );
                es.setOutline(outline);
            } catch (Exception e) {
                es.setOutline("暂无大纲");
            }
            return es;
        }).collect(Collectors.toList());

        esOps.save(esList);
    }


    public SearchPage<FilmElasticSearch> freeSearch(String content, int pageNo, int pageSize) {
        if (content == null || content.isEmpty()) return null;

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .fields("name", "director", "hero", "heroine", "outline")
                        .query(content)
                ))

                .withPageable(PageRequest.of(pageNo, pageSize))
                .build();

        SearchHits<FilmElasticSearch> searchHits = esOps.search(query, FilmElasticSearch.class);
        return SearchHitSupport.searchPageFor(searchHits, query.getPageable());
    }

//    public Map<String, Long> countByClassify() {
//        return new HashMap<>();
//    }

    public Map<String, Long> countByClassify() {
        NativeQuery query = NativeQuery.builder()
                .withAggregation("by_classify", Aggregation.of(a -> a.terms(t -> t.field("classify"))))
                .withMaxResults(0)
                .build();

        SearchHits<FilmElasticSearch> searchHits = esOps.search(query, FilmElasticSearch.class);

        Map<String, Long> result = new HashMap<>();

        if (searchHits.getAggregations() != null) {
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();

            aggregations.aggregationsAsMap().get("by_classify").aggregation()
                    .getAggregate().sterms().buckets().array().forEach(bucket -> {
                        result.put(bucket.key().stringValue(), bucket.docCount());
                    });
        }
        return result;
    }

    public List<FilmElasticSearch> searchByCondition(Map<String, Object> condition) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    if (condition.get("name") != null) {
                        b.must(m -> m.match(t -> t.field("name").query(condition.get("name").toString())));
                    }

                    if (condition.get("classify") != null) {
                        b.must(m -> m.match(t -> t.field("classify").query(condition.get("classify").toString())));
                    }

                    if (condition.get("year") != null) {
                        b.must(m -> m.match(t -> t.field("production").query(condition.get("year").toString())));
                    }

                    return b;
                }))
                .withMaxResults(10)
                .build();

        return esOps.search(query, FilmElasticSearch.class)
                .getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }
}