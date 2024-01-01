package com.example.individualprojectfe.mainpage.domain.client;

import com.example.individualprojectfe.mainpage.domain.user.OrderDto;
import com.example.individualprojectfe.mainpage.domain.user.UserDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final String backendUrl;
    private final RestTemplate restTemplate;

    public UserService(RestTemplate restTemplate) {
        this.backendUrl = "http://localhost:8080";
        this.restTemplate = restTemplate;
    }

    public UserDto getUserByUsername(String username) {
        return restTemplate.getForObject(backendUrl + "/v1/users/username/{username}", UserDto.class, username);
    }

    public List<Long> getUserOrderIds(String username) {
        ResponseEntity<List<Long>> responseEntity = restTemplate.exchange(
                backendUrl + "/v1/users/" + username + "/orders",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
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
            return List.of();
        }
    }

    public List<OrderDto> getUserOrders(String username) {
        List<Long> orderIds = getUserOrderIds(username);
        List<OrderDto> orders = new ArrayList<>();

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

}