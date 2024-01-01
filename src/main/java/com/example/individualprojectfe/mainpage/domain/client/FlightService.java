package com.example.individualprojectfe.mainpage.domain.client;

import com.example.individualprojectfe.mainpage.LoginView;
import com.example.individualprojectfe.mainpage.domain.flight.FlightDto;
import com.example.individualprojectfe.mainpage.domain.flight.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class FlightService {

    private final String backendUrl;
    private final RestTemplate restTemplate;

    @Autowired
    private LoginView loginView;

    public FlightService(RestTemplate restTemplate) {
        this.backendUrl = "http://localhost:8080";
        this.restTemplate = restTemplate;
    }

    public void createFlights(String departure, String arrival, RequestData requestData) {
        restTemplate.postForEntity(backendUrl + "/v1/flights/create/" + departure + "/" + arrival,  requestData, FlightDto.class);
    }

    public List<FlightDto> getAllFlights() {
        FlightDto[] flights = restTemplate.getForObject(backendUrl + "/v1/flights", FlightDto[].class);
        return Arrays.asList(flights);
    }

    public FlightDto getFlightById(Long flightId) {
        return restTemplate.getForObject(backendUrl + "/v1/flights/{flightId}", FlightDto.class, flightId);
    }
}