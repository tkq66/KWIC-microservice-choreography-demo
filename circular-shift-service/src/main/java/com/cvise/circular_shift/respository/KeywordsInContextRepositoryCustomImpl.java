package com.cvise.circular_shift.respository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.cvise.circular_shift.entity.KeywordsInContext;

public class KeywordsInContextRepositoryCustomImpl implements KeywordsInContextRepositoryCustom {
	
	@Autowired
    protected MongoTemplate mongoTemplate;
	
	public void pushUniqueKeywordById(String id, String keywordInContext) {
		mongoTemplate.updateFirst(
				Query.query(Criteria.where("id").is(id)
						.and("keywords").nin(keywordInContext)), 
	            new Update().push("keywords", keywordInContext), KeywordsInContext.class);
	}
}
