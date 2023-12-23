package com.example.individualprojectfe.mainpage.domain.airports;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Airport {
    private String name;
    private String country;
    private String countryCode;
    private String iataCode;
}