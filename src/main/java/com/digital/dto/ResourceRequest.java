package com.digital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
@Getter
@Setter
public class ResourceRequest {
    @NotBlank
    private String title;
    private String description;

    @NotNull
    private Blob fileUrl;

    @NotNull
    private Long classId;
    @NotNull
    private Long sectionId;
    @NotNull
    private Long subjectId;}
