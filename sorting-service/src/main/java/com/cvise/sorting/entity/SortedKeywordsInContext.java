package com.cvise.sorting.entity;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;


public class SortedKeywordsInContext {
	@Id
    public String id;

    public List<String> keywords;

    public SortedKeywordsInContext() {}

    public SortedKeywordsInContext(List<String> keywords) {
    	this.keywords = keywords;
    }
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

    @Override
    public String toString() {
        return String.format(
                "SortedKeywordsInContext[id=%s, keywords='%s']",
                id, keywords.stream().
                map(Object::toString).
                collect(Collectors.joining(",")).toString());
    }

}