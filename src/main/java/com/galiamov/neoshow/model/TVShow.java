package com.galiamov.neoshow.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static com.galiamov.neoshow.model.Validator.validateReleaseDate;
import static com.galiamov.neoshow.model.Validator.validateTitle;

@JsonInclude(Include.NON_NULL)
public class TVShow {

    private static final String TITLE_PROPERTY = "title";
    private static final String RELEASE_DATE_PROPERTY = "release_date";
    private static final String END_DATE_PROPERTY = "end_date";

    private String title;
    private long releaseDate;
    private Long endDate;

    public static TVShow build(Object title, Object releaseDate) {
        return new TVShow((String) title, Long.valueOf(releaseDate.toString()), null);
    }

    @JsonCreator
    public TVShow(@JsonProperty(TITLE_PROPERTY) String title, @JsonProperty(RELEASE_DATE_PROPERTY) Long releaseDate,
            @JsonProperty(END_DATE_PROPERTY) Long endDate) {
        this.title = validateTitle(title);
        this.releaseDate = validateReleaseDate(releaseDate);
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty(RELEASE_DATE_PROPERTY)
    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) throws ParseException {
        this.releaseDate = releaseDate;
    }

    @Nullable
    @JsonProperty(END_DATE_PROPERTY)
    public Long getEndDate() {
        return endDate;
    }

    @JsonIgnore
    public boolean isEndDatePresent() {
        return endDate != null;
    }

    public void setEndDate(@Nullable Long endDate) {
        this.endDate = endDate;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> parameters = new HashMap<>(3);
        parameters.put(TITLE_PROPERTY, this.getTitle());
        parameters.put(RELEASE_DATE_PROPERTY, this.getReleaseDate());
        if (this.isEndDatePresent()) {
            parameters.put(END_DATE_PROPERTY, this.getEndDate());
        }
        return parameters;
    }

}
