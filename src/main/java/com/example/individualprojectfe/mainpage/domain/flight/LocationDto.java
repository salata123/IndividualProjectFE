package com.example.individualprojectfe.mainpage.domain.flight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private Long id;
    private String iataCode;
    private String terminal;
    private String at;
}