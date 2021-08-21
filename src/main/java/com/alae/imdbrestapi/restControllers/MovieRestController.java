package com.alae.imdbrestapi.restControllers;

import com.alae.imdbrestapi.exceptions.NotFoundException;
import com.alae.imdbrestapi.exceptions.ResponseError;
import com.alae.imdbrestapi.models.Movie;
import com.alae.imdbrestapi.utils.ApiFeatures;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieRestController {

    private Movie[] movies;

    //loading the data
    @PostConstruct
    public void init() throws IOException {
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        movies = mapper.readValue(new File("dev-data/movies-details.json"), Movie[].class);
    }

    @GetMapping(path = "")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) Map<String, String> params) {
        var moviesClone = movies.clone();

        var fieldsQueryStr = params.getOrDefault("fields", null);
        var sortQueryStr = params.getOrDefault("sort", null);
        var pageQueryStr = params.getOrDefault("page", null);
        var limitQueryStr = params.getOrDefault("limit", null);

        //sort
        if (sortQueryStr != null) {
            ApiFeatures.sort(Movie.class, moviesClone, sortQueryStr);
        }

        //paginate
        moviesClone = (Movie[]) ApiFeatures.paginate(Movie.class, moviesClone, pageQueryStr, limitQueryStr);

        //select fields
        if(fieldsQueryStr == null){
            return new ResponseEntity<>(moviesClone, HttpStatus.OK);
        }
        var map = ApiFeatures.select(Movie.class, moviesClone, fieldsQueryStr);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @GetMapping(path = "/{movieId}")
    public ResponseEntity<Movie> findById(@Positive @PathVariable int movieId) {
        if (movieId > 250) {
            throw new NotFoundException("no movie with id " + movieId + " is found");
        }
        return new ResponseEntity<>(movies[movieId - 1], HttpStatus.OK);
    }


    @PostMapping(path = "")
    public ResponseEntity<Movie> save(@Valid @RequestBody Movie movie) {
        movie.setId(251);
        return new ResponseEntity<>(movie, HttpStatus.CREATED);
    }


    @PutMapping(path = "")
    public ResponseEntity<Movie> update(@Valid @RequestBody Movie movie) {
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }


    @PatchMapping(path = "")
    public ResponseEntity<Object> patch(@RequestBody Movie movie){
        if(movie.getId() == null){
            throw new NotFoundException("you did not specified the movie id");

        }else if( movie.getId() <= 0 || movie.getId() > 250) {
            throw new NotFoundException("no movie with id " + movie.getId() + " is found");

        }else{
            var updatedMovie = (Movie) ApiFeatures.getUpdatedResource(movies, movie, movie.getId());

            var validationErrors = ApiFeatures.getValidationErrors(updatedMovie);
            if(validationErrors != null){

                var responseError = new ResponseError(HttpStatus.BAD_REQUEST.value(), validationErrors);
                return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(updatedMovie, HttpStatus.OK);
        }
    }


    @DeleteMapping(path = "/{movieId}")
    public ResponseEntity<Movie> deleteById(@Positive @PathVariable int movieId) {
        if (movieId > 250) {
            throw new NotFoundException("no movie with id " + movieId + " is found");
        }
        return new ResponseEntity<>(movies[movieId - 1], HttpStatus.OK);
    }
}
