package com.example.employeemanagementsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
        @NotBlank(message = "empName is required and must not be blank")
        @Size(max = 120, message = "empName must not exceed 120 characters")
        String empName,

        @NotBlank(message = "mail is required and must not be blank")
        @Email(message = "mail must be a valid email address")
        @Size(max = 254, message = "mail must not exceed 254 characters")
        String mail,

        @Positive(message = "salary must be greater than 0")
        int salary,

        @NotBlank(message = "designation is required and must not be blank")
        @Size(max = 120, message = "designation must not exceed 120 characters")
        String designation
) {
}