package com.galiamov.neoshow.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.galiamov.neoshow.model.Validator.validateEmail;
import static com.galiamov.neoshow.model.Validator.validateTitle;

public class Like {

    private String userEmail;
    private String tvShowTitle;

    @JsonCreator
    public Like(@JsonProperty("user_email") String userEmail,
            @JsonProperty("tvshow_title") String tvShowTitle) {
        this.userEmail = validateEmail(userEmail);
        this.tvShowTitle = validateTitle(tvShowTitle);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTvShowTitle() {
        return tvShowTitle;
    }

    public void setTvShowTitle(String tvShowTitle) {
        this.tvShowTitle = tvShowTitle;
    }
}
