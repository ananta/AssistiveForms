package com.anntz.formservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateFormDTO {
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
}