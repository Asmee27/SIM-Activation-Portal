package com.infy.sim.customerservice.controller;

import com.infy.sim.customerservice.dto.CustomerActivationRequest;
import com.infy.sim.customerservice.dto.CustomerDetailsRequest;
import com.infy.sim.customerservice.dto.CustomerValidationRequest;
import com.infy.sim.customerservice.dto.MessageResponse;
import com.infy.sim.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/validation")
    public MessageResponse validate(@Valid @RequestBody CustomerValidationRequest request) {
        return customerService.validate(request);
    }

    @PostMapping("/details")
    public MessageResponse validateDetails(@Valid @RequestBody CustomerDetailsRequest request) {
        return customerService.validateDetails(request);
    }

    @PostMapping("/activate")
    public MessageResponse activate(@Valid @RequestBody CustomerActivationRequest request) {
        return customerService.activate(request);
    }
}
