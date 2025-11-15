package com.restaurant.redismodule.exception;

import lombok.Getter;

@Getter
public class CacheException extends Exception {
    private final String code;
    private final String des;

    public CacheException(String code, String des) {
        this.code = code;
        this.des = des;
    }

}
