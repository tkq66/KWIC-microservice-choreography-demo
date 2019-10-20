package com.cvise.circular_shift.respository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.cvise.circular_shift.entity.KeywordsInContext;

public interface KeywordsInContextRepository extends MongoRepository<KeywordsInContext, String>, KeywordsInContextRepositoryCustom {
}