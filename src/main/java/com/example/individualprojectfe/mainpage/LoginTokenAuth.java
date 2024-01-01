package com.example.individualprojectfe.mainpage;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@SpringComponent
public class LoginTokenAuth extends VerticalLayout {
    private static final String BASE_URL = "http://localhost:8080/v1/loginTokens/checkExpiration/";
    private final RestTemplate restTemplate;
    @Autowired
    public LoginTokenAuth(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isTokenExpired(String username) {
        String url = BASE_URL + username;
        Boolean response = restTemplate.getForObject(url, Boolean.class);
        System.out.println("Is login token expired: " + response); //debug
        return response != null && response;
    }
}
