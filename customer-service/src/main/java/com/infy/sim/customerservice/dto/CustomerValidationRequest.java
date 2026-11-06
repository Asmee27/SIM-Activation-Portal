package com.infy.sim.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/** US04: email + dob lookup of an existing activation request. */
public record CustomerValidationRequest(
        @NotBlank(message = "Email is mandatory")
        @Pattern(regexp = ValidationPatterns.EMAIL, message = ValidationPatterns.EMAIL_MESSAGE)
        String email,

        @NotNull(message = "Date of birth is mandatory")
        @Past(message = "Date of birth should be a past date")
        LocalDate dob) { }
