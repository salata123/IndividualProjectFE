package com.example.individualprojectfe.mainpage.domain.flight;

import lombok.Data;

@Data
public class RequestData {
    private String currencyCode;
    private String originLocationCode;
    private String destinationLocationCode;
    private String departureDate;
    private String departureTime;
}
