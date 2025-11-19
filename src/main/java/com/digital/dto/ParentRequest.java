package com.digital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    private String phone;
    private String address;

    @NotNull
    private String relationship; // "FATHER"|"MOTHER"|"GUARDIAN"
    private List<Long> studentIds;
}
