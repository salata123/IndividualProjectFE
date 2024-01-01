package com.example.individualprojectfe.mainpage.domain.client;

import com.example.individualprojectfe.mainpage.LoginView;
import com.example.individualprojectfe.mainpage.domain.user.OrderDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderService {
    private final String backendUrl;
    private final RestTemplate restTemplate;
    private final CartService cartService;
    private final UserService userService;
    private final LoginView loginView;

    public OrderService(RestTemplate restTemplate, CartService cartService, UserService userService, LoginView loginView) {
        this.backendUrl = "http://localhost:8080";
        this.restTemplate = restTemplate;
        this.cartService = cartService;
        this.userService = userService;
        this.loginView = loginView;
    }

    public void createOrderFromCart() {
        Long cartId = cartService.getCurrentCartId();

        if (cartId != null) {
            List<Long> flightIds = cartService.getCartFlights(cartId);
            if (flightIds != null && !flightIds.isEmpty()) {
                OrderDto orderDto = new OrderDto();
                orderDto.setFlights(flightIds);
                orderDto.setUserId(userService.getUserByUsername(loginView.getLoggedUserUsername()).getId());
                orderDto.setCartId(cartId);
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