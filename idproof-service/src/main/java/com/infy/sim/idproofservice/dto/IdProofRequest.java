package com.infy.sim.idproofservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record IdProofRequest(
        @NotBlank(message = "ID type is mandatory")
        String idType,

        @NotBlank(message = "ID number is mandatory")
        String idNumber,

        @NotBlank(message = "First name is mandatory")
        @Pattern(regexp = "[A-Za-z]{1,15}", message = "First name should contain only alphabets (max 15)")
        String firstName,

        @NotBlank(message = "Last name is mandatory")
        @Pattern(regexp = "[A-Za-z]{1,15}", message = "Last name should contain only alphabets (max 15)")
        String lastName,

        @NotBlank(message = "State is mandatory")
        String state,

        @NotNull(message = "Date of birth is mandatory")
        @Past(message = "Date of birth should be a past date")
        LocalDate dob) { }
