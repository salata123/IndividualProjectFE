package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.domain.flight.FlightDto;
import com.example.individualprojectfe.mainpage.domain.flight.RequestData;
import com.example.individualprojectfe.mainpage.domain.user.CartDto;
import com.example.individualprojectfe.mainpage.domain.user.OrderDto;
import com.example.individualprojectfe.mainpage.domain.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FlightClient {

    private final String backendUrl;
    private final RestTemplate restTemplate;
    @Autowired
    private LoginView loginView;


    public FlightClient(RestTemplate restTemplate) {
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

    public List<Long> getCartFlights(long cartId) {
        CartDto cartDto = restTemplate.getForObject(backendUrl + "/v1/carts/" + cartId, CartDto.class);

        if (cartDto != null) {
            return cartDto.getFlightList();
        } else {
            return null;
        }
    }

    public Long getCurrentCartId() {
        String username = loginView.getLoggedUserUsername();

        try {
            UserDto userDto = getUserByUsername(username);

            if (userDto != null && userDto.getCartId() != null) {
                System.out.println("USER HAS CART");
                return userDto.getCartId();
            } else {
                System.out.println("USER DOES NOT HAVE A CART");
                return null;
            }
        } catch (Exception e) {
            System.out.println("LMAOOOOOO");
            e.printStackTrace();
            return null;
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

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("Flight removed from cart: " + flightId);
        } else {
            System.err.println("Error removing flight from cart. Status code: " + responseEntity.getStatusCodeValue());
        }
    }

    public void addFlightToCart(long cartId, long flightId) {
        restTemplate.postForEntity(backendUrl + "/v1/carts/" + cartId + "/addFlight/" + flightId, null, Void.class);
    }

    public UserDto getUserByUsername(String username) {
        return restTemplate.getForObject(backendUrl + "/v1/users/username/{username}", UserDto.class, username);
    }

    public FlightDto getFlightById(Long flightId) {
        return restTemplate.getForObject(backendUrl + "/v1/flights/{flightId}", FlightDto.class, flightId);
    }

    public List<Long> getUserOrderIds(String username) {
        ResponseEntity<List<Long>> responseEntity = restTemplate.exchange(
                backendUrl + "/v1/users/" + username + "/orders",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Long>>() {
                },
                username
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            List<Long> orderIds = responseEntity.getBody();
            System.out.println(orderIds);
            System.out.println(orderIds.toString());
            System.out.println(orderIds.size());
            return orderIds;
        } else {
            System.err.println("Error fetching order IDs. Status code: " + responseEntity.getStatusCodeValue());
            return List.of(); // or return null if appropriate
        }
    }

    public List<OrderDto> getUserOrders(String username) {
        // Get the list of order IDs for the user
        List<Long> orderIds = getUserOrderIds(username);

        // List to store the orders with details
        List<OrderDto> orders = new ArrayList<>();

        // Iterate over each order ID and fetch order details
        for (Long orderId : orderIds) {
            ResponseEntity<OrderDto> responseEntity = restTemplate.getForEntity(
                    backendUrl + "/v1/orders/" + orderId,
                    OrderDto.class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                OrderDto orderDto = responseEntity.getBody();
                orders.add(orderDto);
                System.out.println("Order details fetched for Order ID " + orderId + ": " + orderDto);
            } else {
                System.err.println("Error fetching order details for Order ID " + orderId +
                        ". Status code: " + responseEntity.getStatusCodeValue());
            }
        }

        return orders;
    }

    public void createOrderFromCart() {
        Long cartId = getCurrentCartId();

        if (cartId != null) {
            // Get the list of flight IDs from the user's cart
            List<Long> flightIds = getCartFlights(cartId);

            if (flightIds != null && !flightIds.isEmpty()) {
                // Create an OrderDto with the flight IDs
                OrderDto orderDto = new OrderDto();
                orderDto.setFlights(flightIds);
                orderDto.setUserId(getUserByUsername(loginView.getLoggedUserUsername()).getId());
                orderDto.setCartId(cartId);

                // Make a POST request to create an order
                ResponseEntity<OrderDto> responseEntity = restTemplate.postForEntity(
                        backendUrl + "/v1/orders",
                        orderDto,
                        OrderDto.class
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Order created and added to the user: " + responseEntity.getBody());
                } else {
                    System.err.println("Error creating order. Status code: " + responseEntity.getStatusCodeValue());
                }
            } else {
                System.out.println("The user's cart is empty.");
            }
        } else {
            System.err.println("Unable to retrieve the current user's cart ID.");
        }
    }
}