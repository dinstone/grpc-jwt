package com.dinstone.grpc.api;

import javax.crypto.SecretKey;

import io.grpc.Context;
import io.jsonwebtoken.security.Keys;

public interface AuthConstant {
    SecretKey JWT_KEY = Keys.hmacShaKeyFor("hello_javaboy_hello_javaboy_hello_javaboy_hello_javaboy_".getBytes());
    Context.Key<String> AUTH_CLIENT_ID = Context.key("clientId");
    String AUTH_HEADER = "Authorization";
    String AUTH_TOKEN_TYPE = "Bearer";
}
