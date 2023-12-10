package com.example.individualprojectfe.mainpage;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("login")
public class LoginView extends VerticalLayout {

    private TextField usernameField;
    private TextField passwordField;

    public LoginView() {
        usernameField = new TextField("Username");
        passwordField = new TextField("Password");
        Button loginButton = new Button("Login", event -> performLogin());

        add(usernameField, passwordField, loginButton);
    }

    private void performLogin() {
        Notification.show("Login functionality will be implemented here.");
    }
}