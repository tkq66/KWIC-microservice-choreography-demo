package com.cvise.input.entity;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;


public class Input {
	@Id
    public String id;

    public List<String> corpus;

    public Input() {}

    public Input(List<String> input) {
    	this.corpus = input;
    }
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getCorpus() {
		return corpus;
	}

	public void setCorpus(List<String> corpus) {
		this.corpus = corpus;
	}

    @Override
    public String toString() {
        return String.format(
                "Input[id=%s, corpus='%s']",
                id, corpus.stream().
                map(Object::toString).
                collect(Collectors.joining(",")).toString());
    }

}