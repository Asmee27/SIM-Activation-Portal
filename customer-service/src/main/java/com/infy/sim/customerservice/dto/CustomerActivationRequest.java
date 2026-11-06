package com.infy.sim.customerservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/** US08: the summary the UI submits on "Activate SIM" (body of POST /customer/activate). */
public record CustomerActivationRequest(
        @NotBlank(message = "First name is mandatory")
        @Pattern(regexp = ValidationPatterns.NAME, message = "First name should contain only alphabets (max 15)")
        String firstName,

        @NotBlank(message = "Last name is mandatory")
        @Pattern(regexp = ValidationPatterns.NAME, message = "Last name should contain only alphabets (max 15)")
        String lastName,

        @NotBlank(message = "Email is mandatory")
        @Pattern(regexp = ValidationPatterns.EMAIL, message = ValidationPatterns.EMAIL_MESSAGE)
        String email,

        @NotNull(message = "Date of birth is mandatory")
        LocalDate dob,

        @NotNull(message = "Address is mandatory")
        @Valid
        AddressDTO address,

        @NotBlank(message = "SIM number is mandatory")
        @Pattern(regexp = "\\d{13}", message = "SIM number should be a 13 digit number")
        String simNumber) { }
