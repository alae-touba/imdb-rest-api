package com.alae.imdbrestapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseError {

    private int statusCode;
    private Map<String, String> errors;
    private Date timestamp;

    public ResponseError(int statusCode, Map<String, String> errors){
        this(statusCode, errors, new Date());
    }
}
