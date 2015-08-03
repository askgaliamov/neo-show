package com.galiamov.neoshow.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

import static com.galiamov.neoshow.model.Validator.validateAge;
import static com.galiamov.neoshow.model.Validator.validateEmail;
import static com.galiamov.neoshow.model.Validator.validateGender;

public class User {

    private static final String EMAIL_PROPERTY = "email";
    private static final String AGE_PROPERTY = "age";
    private static final String GENDER_PROPERTY = "gender";

    private String email;
    private int age;
    private int gender;

    @JsonCreator
    public User(@JsonProperty(EMAIL_PROPERTY) String email, @JsonProperty(AGE_PROPERTY) Integer age,
            @JsonProperty(GENDER_PROPERTY) Integer gender) {
        this.email = validateEmail(email);
        this.age = validateAge(age);
        this.gender = validateGender(gender);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> parameters = new HashMap<>(3);
        parameters.put(EMAIL_PROPERTY, this.getEmail());
        parameters.put(AGE_PROPERTY, this.getAge());
        parameters.put(GENDER_PROPERTY, this.getGender());
        return parameters;
    }


}
