package com.cvise.sorting.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.cvise.sorting.entity.SortedKeywordsInContext;

public interface SortedKeywordsInContextRepository extends MongoRepository<SortedKeywordsInContext, String>, SortedKeywordsInContextRepositoryCustom {
}