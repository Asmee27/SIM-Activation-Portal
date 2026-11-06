package com.infy.sim.customerservice.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * sim-service also requires a JWT, so every outgoing Feign call copies the
 * caller's "Authorization: Bearer ..." header from the incoming request.
 * Works because the Feign call runs on the same thread as the incoming request.
 */
@Configuration
public class FeignTokenRelayConfig {

    @Bean
    public RequestInterceptor bearerTokenRelayInterceptor() {
        return template -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (authorization != null) {
                    template.header(HttpHeaders.AUTHORIZATION, authorization);
                }
            }
        };
    }
}
