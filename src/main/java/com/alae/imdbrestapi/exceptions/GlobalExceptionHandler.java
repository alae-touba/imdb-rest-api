package com.alae.imdbrestapi.exceptions;

import com.alae.imdbrestapi.models.Movie;
import com.alae.imdbrestapi.models.TvShow;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseError> handleConstraintViolation(ConstraintViolationException exception){
        var errors = new HashMap<String, String>();

        exception.getConstraintViolations()
                .forEach(constraintViolation -> {
                    errors.put("message", constraintViolation.getMessage());
                    errors.put("path", constraintViolation.getPropertyPath().toString());
                });

        var responseError = new ResponseError(HttpStatus.BAD_REQUEST.value(), errors);
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseError> handleNotFoundException(NotFoundException exception){
        var errors = new HashMap<String, String>(){{
            put("message", exception.getMessage());
        }};

        var responseError = new ResponseError(HttpStatus.NOT_FOUND.value(), errors);
        return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseError> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception){
        var errors = new HashMap<String ,String>();
        errors.put("message", exception.getMessage());

        var responseError = new ResponseError(HttpStatus.BAD_REQUEST.value(), errors);
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseError> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception){
        var errors = new HashMap<String, String>(){{
            put("message", String.format("http method '%s' is not supported. Supported methods are %s",
                    exception.getMethod(), Arrays.toString(exception.getSupportedMethods())));
        }};

        var responseError = new ResponseError(HttpStatus.METHOD_NOT_ALLOWED.value(), errors);
        return new ResponseEntity<>(responseError, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception){
        var errors = new HashMap<String, String>();
        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(error ->  errors.put(error.getField(), error.getDefaultMessage()));

        var responseError = new ResponseError(HttpStatus.BAD_REQUEST.value(), errors);
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseError> handleHttpMessageNotReadable(HttpMessageNotReadableException exception){
        var errors = new HashMap<String ,String>();

        var mostSpecificCause = exception.getMostSpecificCause();
        var message = mostSpecificCause.getMessage();

        var movieClassFieldsNames = Arrays
                                        .stream(Movie.class.getDeclaredFields())
                                        .map(Field::getName)
                                        .collect(Collectors.toList());

        var tvShowClassFieldNames = Arrays
                                        .stream(TvShow.class.getDeclaredFields())
                                        .map(Field::getName)
                                        .collect(Collectors.toList());

        var allFields = Stream
                            .concat(movieClassFieldsNames.stream(), tvShowClassFieldNames.stream())
                            .collect(Collectors.toSet());


        var fields = String.join("|", allFields);   //id|title|summary....all field from both classes


        var pattern = Pattern.compile("\\[\"("+ fields + ")\"\\]");
        var matcher = pattern.matcher(message);
        if(matcher.find()){
            var tmp = matcher.group();
            var errorField = tmp.substring(2, tmp.length()-2);
            System.out.println(">> " + errorField);

            errors.put(errorField, "bad input for field '" + errorField + "'");
        }else{
            errors.put("message",  message);
        }

        var responseError = new ResponseError(HttpStatus.BAD_REQUEST.value(), errors);
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }

}
