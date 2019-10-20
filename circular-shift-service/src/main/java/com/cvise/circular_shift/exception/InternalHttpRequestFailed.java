package com.cvise.circular_shift.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InternalHttpRequestFailed extends RuntimeException{
	private static final long serialVersionUID = 1L;
    private String msg;

    public InternalHttpRequestFailed() {
    }
    
    public InternalHttpRequestFailed(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}