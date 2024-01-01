package com.example.individualprojectfe.mainpage.domain.client;

import com.example.individualprojectfe.mainpage.LoginView;
import com.example.individualprojectfe.mainpage.domain.user.CartDto;
import com.example.individualprojectfe.mainpage.domain.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CartService {

    private final String backendUrl;
    private final RestTemplate restTemplate;
    private final UserService userService;

    @Autowired
    private LoginView loginView;

    public CartService(RestTemplate restTemplate, UserService userService) {
        this.backendUrl = "http://localhost:8080";
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    public List<Long> getCartFlights(long cartId) {
        String username = loginView.getLoggedUserUsername();
        CartDto cartDto = restTemplate.getForObject(backendUrl + "/v1/carts/" + cartId, CartDto.class);

        if (cartDto != null) {
            return cartDto.getFlightList();
        } else {
            return null;
        }
    }

    public void removeFlightFromCart(long cartId, long flightId) {
        // Make a POST request to remove a flight from the cart
        restTemplate.postForEntity(
                backendUrl + "/v1/carts/{cartId}/removeFlight/{flightId}",
                null,
                CartDto.class,
                cartId,
                flightId
        );
    }

    public void addFlightToCart(long cartId, long flightId) {
        restTemplate.postForEntity(backendUrl + "/v1/carts/" + cartId + "/addFlight/" + flightId, null, Void.class);
    }

    public Long getCurrentCartId() {
        String username = loginView.getLoggedUserUsername();

        try {
            UserDto userDto = userService.getUserByUsername(username);

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
}