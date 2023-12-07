package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.copiedclasses.FlightDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

    public void createFlights() {
        // Make a POST request to create flights
        restTemplate.postForEntity(backendUrl + "/v1/flights/test", null, FlightDto.class);
    }

    public List<FlightDto> getAllFlights() {
        // Make a GET request to retrieve the list of flights
        FlightDto[] flights = restTemplate.getForObject(backendUrl + "/v1/flights", FlightDto[].class);
        return Arrays.asList(flights);
    }
}