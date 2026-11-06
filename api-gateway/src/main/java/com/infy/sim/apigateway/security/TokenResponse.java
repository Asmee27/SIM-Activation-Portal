package com.infy.sim.apigateway.security;

public record TokenResponse(String token, String tokenType, long expiresIn) { }
