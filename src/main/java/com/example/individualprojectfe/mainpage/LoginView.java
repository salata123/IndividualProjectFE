package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.domain.user.UserDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Route("login")
@SpringComponent
@Data
public class LoginView extends VerticalLayout {
    private static LoginView instance;
    private final RestTemplate restTemplate;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;
    private final Button registerButton;
    private String loggedUserUsername;
    private boolean isAuthenticated;

    private LoginView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        usernameField = new TextField("Username");
        passwordField = new PasswordField("Password");
        loginButton = new Button("Login", event -> performLogin());
        registerButton = new Button("Register", event -> performRegistration());

        setAlignItems(Alignment.CENTER);

        Button mainViewButton = new Button("Go to Main View", event -> navigateToMainView());
        add(mainViewButton);

        add(usernameField, passwordField, loginButton, registerButton);
        usernameField.addValueChangeListener(event -> checkUsernameAvailability());
    }

    @Autowired
    public static LoginView getInstance(RestTemplate restTemplate) {
        if (instance == null) {
            instance = new LoginView(restTemplate);
        }
        return instance;
    }

    private void performLogin() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/v1/users/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapToJson(data)))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Notification.show("Login successful");

                loggedUserUsername = username;
                isAuthenticated = true;

                getUI().ifPresent(ui -> ui.navigate(FlightView.class));
            } else {
                Notification.show("Login failed. Check your credentials.");
            }
        } catch (IOException | InterruptedException e) {
            handleLoginException(e);
        }
    }

    private void handleLoginException(Exception e) {
        e.printStackTrace();
        Notification.show("An error occurred during login: " + e.getMessage());
    }

    private void performRegistration() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        // Check if the username is available before attempting registration
        if (checkUsernameAvailability()) {
            // Construct the registration request
            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);

            // Create a new UserDto
            UserDto userDto = new UserDto();
            userDto.setUsername(username);
            userDto.setPassword(password);

            // Send the registration request to the backend
            ResponseEntity<UserDto> registrationResponse = createUser(userDto);

            if (registrationResponse.getStatusCode() == HttpStatus.OK) {
                Notification.show("Registration successful. You can now log in.");
            } else {
                Notification.show("Registration failed. Please try again.");
            }
        } else {
            Notification.show("Username is already taken. Please choose a different username.");
        }
    }

    private boolean checkUsernameAvailability() {
        String username = usernameField.getValue();

        // Send a request to the backend to check if the username is already taken
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/v1/users/checkUsername?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // If the response is "true", the username is taken; if "false", it's available
            return !Boolean.parseBoolean(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Assume an error, so don't allow registration
        }
    }

    private String mapToJson(Map<String, String> data) {
        return "{" +
                data.entrySet().stream()
                        .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
                        .collect(Collectors.joining(","))
                + "}";
    }

    private ResponseEntity<UserDto> createUser(UserDto userDto) {
        try {
            // Send the registration request to the backend
            return restTemplate.postForEntity(
                    "http://localhost:8080/v1/users",
                    userDto,
                    UserDto.class
            );
        } catch (HttpClientErrorException e) {
            handleRegistrationException(e);
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            handleRegistrationException(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private void handleRegistrationException(Exception e) {
        e.printStackTrace();
        Notification.show("An error occurred during registration: " + e.getMessage());
    }

    private void navigateToMainView() {
        getUI().ifPresent(ui -> ui.navigate(FlightView.class));
    }
}