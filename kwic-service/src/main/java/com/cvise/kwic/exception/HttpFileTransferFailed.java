package com.cvise.kwic.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HttpFileTransferFailed extends RuntimeException{
	private static final long serialVersionUID = 1L;
    private String msg;

    public HttpFileTransferFailed() {
    }
    
    public HttpFileTransferFailed(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}