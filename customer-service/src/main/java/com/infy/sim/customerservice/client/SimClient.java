package com.infy.sim.customerservice.client;

import com.infy.sim.customerservice.dto.MessageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for sim-service. "sim-service" is the Eureka service id, so
 * Spring Cloud LoadBalancer resolves the actual host:port at call time.
 */
@FeignClient(name = "sim-service")
public interface SimClient {

    @PostMapping("/sim/activation")
    MessageResponse activate(@RequestBody SimActivationRequest request);

    record SimActivationRequest(String simNumber) { }
}
