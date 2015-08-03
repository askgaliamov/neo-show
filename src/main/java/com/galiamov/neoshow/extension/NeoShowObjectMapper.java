package com.galiamov.neoshow.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galiamov.neoshow.NeoShowException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.galiamov.neoshow.NeoShowException.INVALID_DATE_FORMAT;
import static com.galiamov.neoshow.NeoShowException.INVALID_JSON_FORMAT;
import static com.galiamov.neoshow.NeoShowException.RESULT_PROCESSING_ERROR;

public class NeoShowObjectMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    static {
        objectMapper.setDateFormat(dateFormat);
    }

    static <T> T readValue(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (IOException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NeoShowException) {
                throw (NeoShowException) cause;
            }
            e.printStackTrace();
            throw new NeoShowException(INVALID_JSON_FORMAT);
        }
    }


    static String writeValueAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new NeoShowException(RESULT_PROCESSING_ERROR);
        }
    }


    static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new NeoShowException(INVALID_DATE_FORMAT);
        }
    }

}
