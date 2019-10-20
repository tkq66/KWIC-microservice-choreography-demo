package com.cvise.input.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.cvise.input.entity.Input;

public class InputRepositoryCustomImpl implements InputRepositoryCustom {
	
	@Autowired
    protected MongoTemplate mongoTemplate;
	
	public void pushUniqueCorpusById(String id, String input) {
		mongoTemplate.updateFirst(
				Query.query(Criteria.where("id").is(id)
						.and("corpus").nin(input)), 
	            new Update().push("corpus", input), Input.class);
	}
}
