package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.domain.flight.FlightDto;
import com.example.individualprojectfe.mainpage.domain.flight.RequestData;
import com.example.individualprojectfe.mainpage.domain.user.CartDto;
import com.example.individualprojectfe.mainpage.domain.user.UserDto;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@SpringComponent
public class FlightClient {

    private final String backendUrl;
    private final RestTemplate restTemplate;


    public FlightClient(RestTemplate restTemplate) {
        this.backendUrl = "http://localhost:8080";
        this.restTemplate = restTemplate;
    }

    public void createFlights(RequestData requestData) {
        // Make a POST request to create flights
        restTemplate.postForEntity(backendUrl + "/v1/flights", requestData, FlightDto.class);
    }

    public List<FlightDto> getAllFlights() {
        // Make a GET request to retrieve the list of flights
        FlightDto[] flights = restTemplate.getForObject(backendUrl + "/v1/flights", FlightDto[].class);
        return Arrays.asList(flights);
    }

    public List<Long> getCartFlights(long cartId) {
        // Make a GET request to retrieve the cart
        CartDto cartDto = restTemplate.getForObject(backendUrl + "/v1/carts/" + cartId, CartDto.class);

        // Extract flights from the retrieved cart
        if (cartDto != null) {
            return cartDto.getFlightList();
        } else {
            return null; // Handle the case where the cart is not found
        }
    }

    public void removeFlightFromCart(long cartId, long flightId) {
        // Make a POST request to remove a flight from the cart
        ResponseEntity<CartDto> responseEntity = restTemplate.postForEntity(
                backendUrl + "/v1/carts/{cartId}/removeFlight/{flightId}",
                null,
                CartDto.class,
                cartId,
                flightId
        );

        // Handle the response if needed
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            // Flight removed successfully
            System.out.println("Flight removed from cart: " + flightId);
        } else {
            // Handle the case where the removal was not successful
            System.err.println("Error removing flight from cart. Status code: " + responseEntity.getStatusCodeValue());
        }
    }

    public void addFlightToCart(long cartId, long flightId) {
        // Make a POST request to add a flight to the cart
        restTemplate.postForEntity(backendUrl + "/v1/carts/" + cartId + "/addFlight/" + flightId, null, Void.class);
    }

    public UserDto getUserByUsername(String username) {
        // Make a GET request to retrieve user information by username
        return restTemplate.getForObject(backendUrl + "/v1/users/username/{username}", UserDto.class, username);
    }

    public FlightDto getFlightById(Long flightId) {
        // Make a GET request to retrieve a single flight by its ID
        return restTemplate.getForObject(backendUrl + "/v1/flights/{flightId}", FlightDto.class, flightId);
    }
}