package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.LoginView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private void showLoginNotification() {
        Dialog dialog = new Dialog();
        dialog.add("Your login token has expired. Please login again.");

        Button closeButton = new Button("Close", event -> dialog.close());
        dialog.add(closeButton);

        dialog.open();
    }
}
