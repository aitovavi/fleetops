package com.aitovavi.fleetops.customer.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerCreateRequest(

        @NotBlank(message = "Customer name is required")
        @Size(max = 120, message = "Customer name must not exceed 120 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must have a valid format")
        @Size(max = 120, message = "Email must not exceed 120 characters")
        String email,

        @Pattern(
                regexp = "^\\+?[0-9]{7,20}$",
                message = "Phone number must contain 7 to 20 digits"
        )
        String phoneNumber,

        @Size(max = 120, message = "Company name must not exceed 120 characters")
        String companyName
) {
}