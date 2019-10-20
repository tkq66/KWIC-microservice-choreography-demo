package com.cvise.input.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.cvise.input.entity.Input;

public interface InputRepository extends MongoRepository<Input, String>, InputRepositoryCustom {
}