package com.nikza.socialnetwork.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String s) {
        super(s);
    }
}