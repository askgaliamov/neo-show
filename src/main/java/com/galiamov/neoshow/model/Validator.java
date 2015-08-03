package com.galiamov.neoshow.model;

import com.galiamov.neoshow.NeoShowException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import static com.galiamov.neoshow.NeoShowException.INVALID_AGE;
import static com.galiamov.neoshow.NeoShowException.INVALID_EMAIL;
import static com.galiamov.neoshow.NeoShowException.INVALID_GENDER;
import static com.galiamov.neoshow.NeoShowException.INVALID_RELEASE_DATE;
import static com.galiamov.neoshow.NeoShowException.INVALID_TITLE;

public class Validator {

    private static final EmailValidator emailValidator = EmailValidator.getInstance();

    static String validateTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new NeoShowException(INVALID_TITLE);
        }
        return title;
    }

    static long validateReleaseDate(Long releaseDate) {
        if (releaseDate == null) {
            throw new NeoShowException(INVALID_RELEASE_DATE);
        }
        return releaseDate;
    }

    public static String validateEmail(String email) {
        if (!emailValidator.isValid(email)) {
            throw new NeoShowException(INVALID_EMAIL);
        }
        return email;
    }

    static Integer validateAge(Integer age) {
        if (age == null || age < 1 || age > 120) {
            throw new NeoShowException(INVALID_AGE);
        }
        return age;
    }

    static Integer validateGender(Integer gender) {
        if (gender == null || gender < 1 || gender > 2) {
            throw new NeoShowException(INVALID_GENDER);
        }
        return gender;
    }

}
