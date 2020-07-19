package com.alae.imdbrestapi.restControllers;

import com.alae.imdbrestapi.exceptions.NotFoundException;
import com.alae.imdbrestapi.exceptions.ResponseError;
import com.alae.imdbrestapi.models.TvShow;
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
@RequestMapping("/api/shows")
@Validated
public class TvShowRestController {

    private TvShow[] shows;

    @PostConstruct
    public void init() throws IOException {
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        shows = mapper.readValue(new File("dev-data/tv-shows-details.json"), TvShow[].class);
    }


    @GetMapping(path = "")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) Map<String, String> params) {
        var showsClone = shows.clone();

        var fieldsQueryStr = params.getOrDefault("fields", null);
        var sortQueryStr = params.getOrDefault("sort", null);
        var pageQueryStr = params.getOrDefault("page", null);
        var limitQueryStr = params.getOrDefault("limit", null);

        //sort
        if (sortQueryStr != null) {
            ApiFeatures.sort(TvShow.class, showsClone, sortQueryStr);
        }

        //paginate
        showsClone = (TvShow[]) ApiFeatures.paginate(TvShow.class, showsClone, pageQueryStr, limitQueryStr);

        //select fields
        if(fieldsQueryStr == null){
            return new ResponseEntity<>(showsClone, HttpStatus.OK);
        }
        var map = ApiFeatures.select(TvShow.class, showsClone, fieldsQueryStr);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @GetMapping(path = "/{showId}")
    public ResponseEntity<TvShow> findById(@Positive @PathVariable int showId) {
        if (showId > shows.length) {
            throw new NotFoundException("no tv show with id " + showId + " is found");
        }
        return new ResponseEntity<>(shows[showId - 1], HttpStatus.OK);
    }


    @PostMapping(path = "")
    public ResponseEntity<TvShow> save(@Valid @RequestBody TvShow show) {
        show.setId(251);
        return new ResponseEntity<>(show, HttpStatus.CREATED);
    }


    @PutMapping(path = "")
    public ResponseEntity<TvShow> update(@Valid @RequestBody TvShow show) {
        return new ResponseEntity<>(show, HttpStatus.OK);
    }


    @PatchMapping(path = "")
    public ResponseEntity<Object> patch(@RequestBody TvShow show){
        if(show.getId() == null){
            throw new NotFoundException("you did not specified the show id");

        }else if( show.getId() <=0 || show.getId() > 250) {
            throw new NotFoundException("no show with id " + show.getId() + " is found");

        }else{
            var updatedShow = (TvShow) ApiFeatures.getUpdatedResource(shows, show, show.getId());

            var validationErrors = ApiFeatures.getValidationErrors(updatedShow);
            if(validationErrors != null){

                var responseError = new ResponseError(HttpStatus.BAD_REQUEST.value(), validationErrors);
                return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(updatedShow, HttpStatus.OK);
        }
    }


    @DeleteMapping(path = "/{showId}")
    public ResponseEntity<TvShow> deleteById(@Positive @PathVariable int showId) {
        if (showId > 250) {
            throw new NotFoundException("no tv show with id " + showId + " is found");
        }
        return new ResponseEntity<>(shows[showId - 1], HttpStatus.OK);
    }
}
