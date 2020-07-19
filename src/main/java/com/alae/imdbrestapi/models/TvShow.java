package com.alae.imdbrestapi.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TvShow{
    private Integer id;

    @NotBlank(message = "title must not be blank")
    private String title;

    @NotBlank(message = "summary must not be blank")
    private String summary;

    @Max(value = 2020, message = "invalid release Year")
    @Min(value = 1900, message = "invalid release Year")
    private Integer releaseYear;

    @Min(value = 0, message = "minimum rating value is 0")
    @Max(value = 10, message = "maximum rating value is 10")
    private Double ratingValue;

    @Min(value = 1, message = "minimum rating count is 1")
    private Integer ratingCount;

    @JacksonXmlElementWrapper(localName = "genres")
    @JacksonXmlProperty(localName = "genre")
    private List<@NotBlank(message="genre must not be blank")String> genres;

    @JacksonXmlElementWrapper(localName = "stars")
    @JacksonXmlProperty(localName = "star")
    private List<@NotBlank(message="start name must not be blank") String> stars;

    @Min(value = 1, message = "minimum runtime of a movie is 1 mn")
    private Double runtime;

    private String creator;

    @JacksonXmlElementWrapper(localName = "languages")
    @JacksonXmlProperty(localName = "language")
    private List<@NotBlank(message="language must not be blank") String> languages;

    @JacksonXmlElementWrapper(localName = "countries")
    @JacksonXmlProperty(localName = "country")
    private List<@NotBlank(message="country name must not be blank") String> countries;

    @Min(value = 1, message = "minimum number of episodes is 1")
    private Integer episodes;
}
