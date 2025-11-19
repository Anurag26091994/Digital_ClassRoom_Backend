package com.digital.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class GeneratePdfReportRequest {

    private LocalDate fromDate;

    private LocalDate toDate;

    private String rollNo;
}
