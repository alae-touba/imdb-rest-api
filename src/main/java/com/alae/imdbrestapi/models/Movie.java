package com.alae.imdbrestapi.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie{
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
    private Double ratingCount;

    @JacksonXmlElementWrapper(localName = "genres")
    @JacksonXmlProperty(localName = "genre")
    private List<@NotBlank(message="genre must not be blank")String> genres;

    @JacksonXmlElementWrapper(localName = "stars")
    @JacksonXmlProperty(localName = "star")
    private List<@NotBlank(message="star name must not be blank") String> stars;

    @Min(value = 1, message = "minimum runtime of a movie is 1 mn")
    private Double runtime;

    private String director;

    @JacksonXmlElementWrapper(localName = "languages")
    @JacksonXmlProperty(localName = "language")
    private List<@NotBlank(message="language must not be blank") String> languages;

}
