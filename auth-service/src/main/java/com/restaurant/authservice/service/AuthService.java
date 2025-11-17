package com.restaurant.authservice.service;

import com.restaurant.authservice.factory.AuthFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthFactory authFactory;


    public AuthService(AuthFactory authFactory) {
        this.authFactory = authFactory;
    }

}
