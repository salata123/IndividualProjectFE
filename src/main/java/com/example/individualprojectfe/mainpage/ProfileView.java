package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.domain.flight.FlightDto;
import com.example.individualprojectfe.mainpage.domain.user.OrderDto;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

@UIScope
@Route("profile")
@PreserveOnRefresh
@CssImport("./styles.css")
public class ProfileView extends VerticalLayout {

    private FlightClient flightClient;
    private LoginView loginView;
    private Grid<FlightDto> cartGrid;
    private Grid<OrderDto> ordersGrid; // Add ordersGrid declaration

    @Autowired
    public ProfileView(FlightClient flightClient, LoginView loginView) {
        this.flightClient = flightClient;
        this.loginView = loginView;
        initProfileView();
    }

    private void initProfileView() {
        try {
            // Add a header to your profile view
            add(new H3("Welcome, " + loginView.getLoggedUserUsername()));

            // Create a button to search flights
            Button searchFlightsButton = new Button("Search Flights");

            // Set the button click listener to navigate to FlightView.class
            searchFlightsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(FlightView.class)));

            // Create a vertical layout to center the composition
            VerticalLayout centerLayout = new VerticalLayout();
            centerLayout.setHorizontalComponentAlignment(Alignment.CENTER, searchFlightsButton);

            cartGrid = new Grid<>(FlightDto.class);
            cartGrid.setColumns("id", "numberOfBookableSeats", "price");
            cartGrid.getColumnByKey("numberOfBookableSeats").setHeader("Seats available");
            cartGrid.addComponentColumn(this::createRemoveFromCartButton)
                    .setHeader("Remove")
                    .setFlexGrow(0)
                    .setWidth("150px");
            cartGrid.getColumnByKey("id").setWidth("auto").setFlexGrow(0);
            cartGrid.getColumnByKey("numberOfBookableSeats").setWidth("auto").setFlexGrow(0);
            cartGrid.getColumnByKey("price").setWidth("auto").setFlexGrow(0);
            cartGrid.addClassName("responsive-grid");

            // Create and add the cart list using the cartGrid
            VerticalLayout cartList = new VerticalLayout();
            cartList.add(new H3("Cart"));
            cartList.add(cartGrid); // Add the cartGrid to the cartList
            refreshCart(); // Refresh the cartGrid with data

            // Create a "Buy" button
            Button buyButton = new Button("Buy");
            buyButton.addClickListener(e -> handleBuyButtonClick());

            // Add the "Buy" button to the cartList
            cartList.add(buyButton);

            centerLayout.add(cartList);


            // Create and add the orders list using the getUserOrders method
            ordersGrid = new Grid<>(OrderDto.class); // Initialize ordersGrid
            ordersGrid.addColumn(OrderDto::getId).setHeader("Order ID");

            // Create a custom column for the number of flights
            ordersGrid.addColumn(orderDto -> orderDto.getFlights() != null ? orderDto.getFlights().size() : 0)
                    .setHeader("Number of Flights")
                    .setKey("numberOfFlights");

            // Create and add the orders list using the ordersGrid
            VerticalLayout ordersList = new VerticalLayout();
            ordersList.add(new H3("Orders"));
            ordersList.add(ordersGrid); // Add the ordersGrid to the ordersList
            refreshOrdersList(); // Refresh the ordersGrid with data

            centerLayout.add(ordersList);
            add(searchFlightsButton, centerLayout);


        } catch (Exception e) {
            // Handle the exception appropriately
            e.printStackTrace();
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

    private void refreshOrdersList() {
        try {
            // Fetch and update the orders data from the backend
            List<OrderDto> orders = flightClient.getUserOrders(loginView.getLoggedUserUsername());

            // Update the ordersGrid with the new orders
            ordersGrid.setItems(orders);

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error refreshing orders list. Please try again.");
        }
    }

    private Button createRemoveFromCartButton(FlightDto flightDto) {
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

    private void handleBuyButtonClick() {
        try {
            // Call the backend API to create an order from the user's cart
            flightClient.createOrderFromCart();

            // Clear the cart by removing every flight from it
            clearCart();

            // Refresh the cartGrid with the updated cart data
            refreshCart();

            // Refresh the ordersList with the updated order data
            refreshOrdersList();

            // Show a notification after successful purchase
            Notification.show("Purchase successful!");

            UI.getCurrent().navigate(ProfileView.class);

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error processing purchase. Please try again.");
        }
    }

    private void clearCart() {
        try {
            // Get the current cartId
            Long cartId = flightClient.getCurrentCartId();

            if (cartId != null) {
                // Get the list of flight IDs from the user's cart
                List<Long> flightIds = flightClient.getCartFlights(cartId);

                // Remove each flight from the cart
                if (flightIds != null) {
                    for (Long flightId : flightIds) {
                        flightClient.removeFlightFromCart(cartId, flightId);
                    }
                }

                // Refresh the cartGrid after clearing the cart
                refreshCart();
            } else {
                System.err.println("Unable to retrieve the current user's cart ID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error clearing cart. Please try again.");
        }
    }



}
