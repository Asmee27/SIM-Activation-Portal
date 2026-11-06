package com.infy.sim.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** US05: first/last name must match the details captured at purchase. */
public record CustomerDetailsRequest(
        @NotBlank(message = "Email is mandatory")
        @Pattern(regexp = ValidationPatterns.EMAIL, message = ValidationPatterns.EMAIL_MESSAGE)
        String email,

        @NotBlank(message = "First name is mandatory")
        @Pattern(regexp = ValidationPatterns.NAME, message = "First name should contain only alphabets (max 15)")
        String firstName,

        @NotBlank(message = "Last name is mandatory")
        @Pattern(regexp = ValidationPatterns.NAME, message = "Last name should contain only alphabets (max 15)")
        String lastName) { }
