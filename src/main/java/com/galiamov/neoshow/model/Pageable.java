package com.galiamov.neoshow.model;

import java.util.HashMap;
import java.util.Map;

public class Pageable {

    private static final int DEFAULT_SKIP = 0;
    private static final int DEFAULT_LIMIT = 10;

    private int skip;
    private int limit;

    public Pageable(Integer skip, Integer limit) {
        this.skip = skip == null ? DEFAULT_SKIP : skip;
        this.limit = limit == null ? DEFAULT_LIMIT : limit;
    }

    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>(2);
        stringObjectHashMap.put("skip", skip);
        stringObjectHashMap.put("limit", limit);
        return stringObjectHashMap;
    }
}
