package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.domain.airports.AirportDatabase;
import com.example.individualprojectfe.mainpage.domain.airports.Currency;
import com.example.individualprojectfe.mainpage.domain.flight.FlightDto;
import com.example.individualprojectfe.mainpage.domain.flight.RequestData;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route("flights")
@PreserveOnRefresh
@SpringComponent
@EnableScheduling
public class FlightView extends VerticalLayout implements BeforeEnterObserver {
    private final FlightClient flightClient;
    private final Grid<FlightDto> flightGrid;
    private final ComboBox<Currency> currencyCodeComboBox;
    private final ComboBox<String> originLocationCodeComboBox;
    private final ComboBox<String> destinationLocationCodeComboBox;
    private final AirportDatabase airportDatabase;
    private final DatePicker departureDateField;
    private final TimePicker departureTimeField;
    private final Grid<FlightDto> cartGrid;
    private final Span usernameLabel;
    private final VerticalLayout cartHeaderLayout;
    private HorizontalLayout topBar;
    private final Button logoutButton = new Button("Logout", event -> performLogout());
    private final Button profileButton = new Button("Profile", event -> navigateToProfile());
    private final Button loginButton = new Button("Login", event -> navigateToLogin());
    private LoginView loginView;
    @Autowired
    private LoginTokenAuth loginTokenAuth;
    @Autowired
    public FlightView(FlightClient flightClient, LoginView loginView) {
        this.flightClient = flightClient;
        this.loginView = loginView;
        airportDatabase = new AirportDatabase();

        Button createFlightsButton = new Button("Show Flights", event -> createFlights());
        loginButton.getElement().getThemeList().add("primary");

        Button isLoggedInButton = new Button("Is user logged in?", this::checkUserLoggedIn);
        isLoggedInButton.getElement().getThemeList().add("primary");

        flightGrid = new Grid<>(FlightDto.class);
        currencyCodeComboBox = new ComboBox<>("Currency Code", Arrays.asList(Currency.values()));
        currencyCodeComboBox.setValue(Currency.USD);
        originLocationCodeComboBox = new ComboBox<>("Origin Airport");
        originLocationCodeComboBox.setItems(airportDatabase.getAirportNames());

        // Create ComboBox for destinationLocationCodeField
        destinationLocationCodeComboBox = new ComboBox<>("Destination Airport");
        destinationLocationCodeComboBox.setItems(airportDatabase.getAirportNames());

        departureDateField = new DatePicker("Departure Date");
        departureTimeField = new TimePicker("Departure Time");

        cartGrid = new Grid<>(FlightDto.class);
        cartGrid.setColumns("id", "numberOfBookableSeats", "price");
        cartGrid.getColumnByKey("numberOfBookableSeats").setHeader("Seats available");
        cartGrid.setWidth("50%");
        cartGrid.addComponentColumn(this::createRemoveFromCartButton)
                .setHeader("Remove")
                .setFlexGrow(0)
                .setWidth("150px");

        topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setJustifyContentMode(JustifyContentMode.END);

        usernameLabel = new Span();
        cartHeaderLayout = new VerticalLayout();
        updateCartVisibility();

        updateButtons();

        flightGrid.setColumns("id", "numberOfBookableSeats");
        flightGrid.getColumnByKey("numberOfBookableSeats").setHeader("Seats available");
        flightGrid.addColumn(flightDto -> flightDto.getSegments().size())
                .setHeader("Segments");
        flightGrid.addColumn(FlightDto::getPrice)
                .setHeader("Price")
                .setRenderer(new TextRenderer<>(priceDto ->
                        String.format("%s %s", priceDto.getPrice().getTotal(), priceDto.getPrice().getCurrency())));
        flightGrid.addComponentColumn(this::createAddToCartButton)
                .setHeader("Add to Cart")
                .setFlexGrow(0) // Ensure the column doesn't take additional space
                .setWidth("150px");
        flightGrid.setWidth("50%");


        HorizontalLayout inputLayout = new HorizontalLayout(currencyCodeComboBox, originLocationCodeComboBox, destinationLocationCodeComboBox, departureDateField, departureTimeField);
        inputLayout.setSpacing(true);

        HorizontalLayout buttonLayout = new HorizontalLayout(createFlightsButton, isLoggedInButton);
        buttonLayout.setSpacing(true);

        add(topBar, inputLayout, buttonLayout, flightGrid, cartHeaderLayout, cartGrid);
        expand(flightGrid);
        setSizeFull();
        setHorizontalComponentAlignment(Alignment.CENTER, flightGrid, cartGrid, inputLayout, buttonLayout);
        setSpacing(true);

        refreshFlights();
    }

    private void refreshFlights() {
        try {
            List<FlightDto> flights = flightClient.getAllFlights();
            flightGrid.setItems(flights);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error fetching flights. Please try again.");
        }
    }

    private Component createAddToCartButton(FlightDto flightDto) {
        Button addToCartButton = new Button("Add to Cart", event -> addToCart(flightDto));
        addToCartButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return addToCartButton;
    }

    private void addToCart(FlightDto flightDto) {
        try {
            // Call the backend API to get the detailed information of the selected flight
            FlightDto selectedFlight = flightClient.getFlightById(flightDto.getId());

            // Check if the selectedFlight is not null
            if (selectedFlight != null) {
                // Call the backend API to add the selected flight to the cart
                flightClient.addFlightToCart(flightClient.getCurrentCartId(), selectedFlight.getId());

                // Refresh the cartGrid with the updated cart data
                refreshCart();

                Notification.show("Flight added to cart: " + selectedFlight.getId());
            } else {
                // Handle the case where fetching detailed flight information failed
                Notification.show("Error fetching flight details. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error adding flight to cart. Please try again.");
        }
    }

    private Component createRemoveFromCartButton(FlightDto flightDto) {
        Button removeFromCartButton = new Button("Remove", event -> removeFromCart(flightDto));
        removeFromCartButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return removeFromCartButton;
    }

    private void removeFromCart(FlightDto flightDto) {
        try {
            // Call the backend API to remove the selected flight from the cart
            flightClient.removeFlightFromCart(flightClient.getCurrentCartId(), flightDto.getId());

            // Refresh the cartGrid with the updated cart data
            refreshCart();

            Notification.show("Flight removed from cart: " + flightDto.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error removing flight from cart. Please try again.");
        }
    }

    private void refreshCart() {
        try {
            // Fetch and update the cart data from the backend
            List<Long> cartFlightIds = flightClient.getCartFlights(flightClient.getCurrentCartId());

            // Get FlightDto objects from flight ids
            List<FlightDto> cartFlights = new ArrayList<>();
            if (cartFlightIds != null) {
                for (Long flightId : cartFlightIds) {
                    FlightDto cartFlight = flightClient.getFlightById(flightId);
                    if (cartFlight != null) {
                        cartFlights.add(cartFlight);
                    } else {
                        // Handle the case where fetching detailed flight information failed
                        Notification.show("Error fetching flight details. Please try again.");
                    }
                }
            }

            // Update the cartGrid with the new cartFlights
            cartGrid.setItems(cartFlights);

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error refreshing cart. Please try again.");
        }
    }

    private void createFlights() {
        try {
            String currencyCode = currencyCodeComboBox.getValue().toString();
            String originAirportName = originLocationCodeComboBox.getValue();
            String originLocationCode = airportDatabase.getIataCodeByName(originAirportName);
            String destinationAirportName = destinationLocationCodeComboBox.getValue();
            String destinationLocationCode = airportDatabase.getIataCodeByName(destinationAirportName);
            String departureDate = departureDateField.getValue().toString();
            String departureTime = departureTimeField.getValue().toString() + ":00";

            RequestData requestData = new RequestData();
            requestData.setCurrencyCode(currencyCode);
            requestData.setOriginLocationCode(originLocationCode);
            requestData.setDestinationLocationCode(destinationLocationCode);
            requestData.setDepartureDate(departureDate);
            requestData.setDepartureTime(departureTime);

            if (isDepartureDateValid(departureDateField.getValue())) {
                flightClient.createFlights(requestData);
                refreshFlights();
                Notification.show("Flights created successfully.");
            } else {
                Notification.show("Departure date cannot be set to a past date. Please choose a valid date.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error creating flights. Please try again.");
        }
    }

    private boolean isDepartureDateValid(LocalDate departureDate) {
        return departureDate != null && !departureDate.isBefore(LocalDate.now());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(loginView.getLoggedUserUsername()!=null){
            isUserAuthenticated();
        }
        updateButtons();
        updateCartVisibility();
    }

    private void updateCartVisibility() {
        boolean isAuthenticated = isUserAuthenticated();

        if (isAuthenticated) {
            // Fetch the cartId from the backend whenever needed
            Long currentCartId = flightClient.getCurrentCartId();

            if (currentCartId != null) {
                showCart();
                refreshCart();
                // Set the usernameLabel text and then create the cartTitle
                usernameLabel.setText(loginView.getLoggedUserUsername());
                Span cartTitle = new Span(usernameLabel.getText() + "'s cart:");

                usernameLabel.setVisible(cartGrid.isVisible());
                cartHeaderLayout.removeAll(); // Clear existing components
                cartHeaderLayout.add(cartTitle);
                cartHeaderLayout.setAlignItems(Alignment.CENTER);
                cartHeaderLayout.setVisible(true); // Show the cart header when the user is logged in

                // Show the cart grid when the user is logged in
                cartGrid.setVisible(true);
            } else {
                hideCart();
                cartHeaderLayout.setVisible(false); // Hide the cart header when the user is not logged in

                // Hide the cart grid when the user is not logged in
                cartGrid.setVisible(false);
            }

            // Add debug information
            System.out.println("Update Cart Visibility - User Authenticated: " + isAuthenticated);
            // No need to print the cartId here since it's not stored as a class field
        }
    }

    private void showCart() {
        cartGrid.setVisible(true);
    }

    private void hideCart() {
        cartGrid.setVisible(false);
    }

    private boolean isUserAuthenticated() {
        boolean isAuthenticated = loginView.isAuthenticated();

        // Log the current user's authentication status and username
        if (isAuthenticated) {
            System.out.println("User " + loginView.getLoggedUserUsername() + " is logged in");
            loginTokenAuth.isTokenExpired(loginView.getLoggedUserUsername());
        } else {
            System.out.println("No user is currently logged in");
        }

        return isAuthenticated;
    }

    private void performLogout() {
        loginView.setLoggedUserUsername(null);
        loginView.setAuthenticated(false);
        updateButtons();
        getUI().ifPresent(ui -> ui.navigate(LoginView.class));
    }

    private void navigateToProfile() {
        getUI().ifPresent(ui -> ui.navigate(ProfileView.class));
    }

    private void navigateToLogin() {
        getUI().ifPresent(ui -> ui.navigate(LoginView.class));
    }

    private void checkUserLoggedIn(ClickEvent<Button> event) {
        if (isUserAuthenticated()) {
            Notification.show("User is logged in: " + loginView.getLoggedUserUsername());
        } else {
            Notification.show("User is not logged in.");
        }
    }

    private void updateButtons() {
        topBar.removeAll(); // Clear existing components

        if (isUserAuthenticated()) {
            topBar.add(profileButton, logoutButton);
        } else {
            topBar.add(loginButton);
        }
    }
}