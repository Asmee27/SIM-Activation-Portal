package com.infy.sim.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** US06: shipping address. State is checked against IndianStates in the service. */
public record AddressDTO(
        @NotBlank(message = "Address is mandatory")
        @Size(max = 25, message = "Address should be at most 25 characters")
        String address,

        @NotBlank(message = "PIN code is mandatory")
        @Pattern(regexp = "\\d{6}", message = "PIN code should be a 6 digit number")
        String pinCode,

        @NotBlank(message = "City is mandatory")
        @Pattern(regexp = "[A-Za-z ]+", message = "City should not contain special characters")
        String city,

        @NotBlank(message = "State is mandatory")
        String state) { }
