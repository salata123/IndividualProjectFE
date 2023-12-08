package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.copiedclasses.FlightDto;
import com.example.individualprojectfe.mainpage.copiedclasses.RequestData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class FlightClient {

    private final String backendUrl;
    private final RestTemplate restTemplate;


    public FlightClient(RestTemplate restTemplate) {
        this.backendUrl = "http://localhost:8080";
        this.restTemplate = restTemplate;
    }

    public void createFlights(RequestData requestData) {
        // Make a POST request to create flights
        restTemplate.postForEntity(backendUrl + "/v1/flights/test", requestData, FlightDto.class);
    }

    public List<FlightDto> getAllFlights() {
        // Make a GET request to retrieve the list of flights
        FlightDto[] flights = restTemplate.getForObject(backendUrl + "/v1/flights", FlightDto[].class);
        return Arrays.asList(flights);
    }
}