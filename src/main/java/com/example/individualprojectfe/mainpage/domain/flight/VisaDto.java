package com.example.individualprojectfe.mainpage.domain.flight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisaDto {
    private Long id;
    private Long flightId;
    private String visaType;
}
