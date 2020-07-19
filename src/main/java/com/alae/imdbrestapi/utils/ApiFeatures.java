package com.alae.imdbrestapi.utils;

import com.alae.imdbrestapi.models.Movie;
import com.alae.imdbrestapi.models.TvShow;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections4.comparators.ComparatorChain;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class ApiFeatures {

    //sort resources in place
    public static void sort(Class cls, Object[] resources, String sortQueryStr){
        var allowedSortFields = new ArrayList<>(
                Arrays.asList("id", "title", "summary", "releaseYear", "ratingCount", "ratingValue", "runtime")
        );

        if(cls.equals(TvShow.class)){
            allowedSortFields.add("episodes");
            allowedSortFields.add("creator");
        }else if(cls.equals(Movie.class)){
            allowedSortFields.add("director");
        }

        var sortFields = sortQueryStr.split(",");

        var comparatorChain = new ComparatorChain<>();

        for (var sortField : sortFields) {
            if (allowedSortFields.contains(sortField)) {
                comparatorChain.addComparator(new BeanComparator<>(sortField), true); //desc order
            }
        }

        Arrays.sort(resources, comparatorChain);
    }

    public static Object[] paginate(Class cls, Object[] resources, String pageQueryStr, String limitQueryStr){
        var page = 1; //default
        var limit = resources.length; //250 by default

        if (pageQueryStr != null) {
            page = Integer.parseInt(pageQueryStr);
        }

        if (limitQueryStr != null) {
            limit = Integer.parseInt(limitQueryStr);
        }

        var skip = (page - 1) * limit;

        if (skip < resources.length) {
            if (skip + limit <= resources.length) {
                return Arrays.copyOfRange(resources, skip, skip + limit);
            } else {
                return Arrays.copyOfRange(resources, skip, resources.length);
            }
        }

        return resources;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Object>[] select(Class cls, Object[] resources, String fieldsQueryStr){
        var classFields = Arrays
                                .stream(cls.getDeclaredFields())
                                .map(Field::getName)
                                .collect(Collectors.toList());


        var wantedFields = fieldsQueryStr.split(",");

        int i = 0;
        var map = (HashMap<String, Object>[]) new HashMap[resources.length];

        for (var resource : resources) {
            var m = new HashMap<String, Object>();

            for (var wantedField : wantedFields) {
                if (classFields.contains(wantedField)) {
                    try {
                        var field = cls.getDeclaredField(wantedField);
                        field.setAccessible(true);
                        m.put(wantedField, field.get(resource)); //field.get(obj) <=> obj.getField()
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
            map[i++] = m;

        }

        return map;
    }

    //for patch method
    public static Object getUpdatedResource(Object[] resources, Object resource, int id){
        var updatedResource = resources[id-1]; //the resource the client wants to update

        var declaredFields = Movie.class.getDeclaredFields();

        if(resource instanceof TvShow){
            declaredFields = TvShow.class.getDeclaredFields();
        }


        for(var declaredField: declaredFields){
            if(!declaredField.getName().equals("id")){
                try {
                    declaredField.setAccessible(true);
                    if(  declaredField.get(resource) != null ){
                        declaredField.set(updatedResource, declaredField.get(resource));
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return updatedResource;
    }

    //for path method
    public static HashMap<String, String> getValidationErrors(Object resource){

        var factory = Validation.buildDefaultValidatorFactory();
        var validator = factory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(resource);


        if(!violations.isEmpty()){
            var errors = new HashMap<String, String>();

            for (var violation : violations) {
                errors.put("path: " + violation.getPropertyPath().toString(), violation.getMessage());
            }

            return errors;
        }

        return null;
    }
}
