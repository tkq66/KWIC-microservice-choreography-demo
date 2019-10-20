package com.cvise.circular_shift.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileReadException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String msg;

    public FileReadException() {
    }
    
    public FileReadException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}