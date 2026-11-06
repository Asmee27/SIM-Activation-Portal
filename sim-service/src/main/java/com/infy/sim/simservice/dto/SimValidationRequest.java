package com.infy.sim.simservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SimValidationRequest(
        @NotBlank(message = "SIM number is mandatory")
        @Pattern(regexp = "\\d{13}", message = "SIM number should be a 13 digit number")
        String simNumber,

        @NotBlank(message = "Service number is mandatory")
        @Pattern(regexp = "\\d{10}", message = "Service number should be a 10 digit number")
        String serviceNumber) { }