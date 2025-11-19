package com.digital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentProfileUpdateRequest {

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;

    @Size(max = 100, message = "Street must not exceed 100 characters")
    private String street;

    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;

    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;

    @Pattern(regexp = "^[0-9]{5,10}$", message = "Pin code must be between 5 and 10 digits")
    private String pinCode;
}
