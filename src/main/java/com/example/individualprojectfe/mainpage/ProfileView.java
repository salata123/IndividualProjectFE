package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.domain.flight.FlightDto;
import com.example.individualprojectfe.mainpage.domain.user.OrderDto;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

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
    private Grid<OrderDto> ordersGrid;

    @Autowired
    public ProfileView(FlightClient flightClient, LoginView loginView) {
        this.flightClient = flightClient;
        this.loginView = loginView;
        initProfileView();
    }

    private void initProfileView() {
        try {
            add(new H3("Welcome, " + loginView.getLoggedUserUsername()));
            Button searchFlightsButton = new Button("Search Flights");
            searchFlightsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(FlightView.class)));
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

            VerticalLayout cartList = new VerticalLayout();
            cartList.add(new H3("Cart"));
            cartList.add(cartGrid);
            refreshCart();

            Button buyButton = new Button("Buy");
            buyButton.addClickListener(e -> handleBuyButtonClick());
            cartList.add(buyButton);
            centerLayout.add(cartList);

            ordersGrid = new Grid<>(OrderDto.class);
            ordersGrid.addColumn(OrderDto::getId).setHeader("Order ID");
            ordersGrid.addColumn(orderDto -> orderDto.getFlights() != null ? orderDto.getFlights().size() : 0)
                    .setHeader("Number of Flights")
                    .setKey("numberOfFlights");

            VerticalLayout ordersList = new VerticalLayout();
            ordersList.add(new H3("Orders"));
            ordersList.add(ordersGrid);
            refreshOrdersList();

            centerLayout.add(ordersList);
            add(searchFlightsButton, centerLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshCart() {
        try {
            List<Long> cartFlightIds = flightClient.getCartFlights(flightClient.getCurrentCartId());

            List<FlightDto> cartFlights = new ArrayList<>();
            if (cartFlightIds != null) {
                for (Long flightId : cartFlightIds) {
                    FlightDto cartFlight = flightClient.getFlightById(flightId);
                    if (cartFlight != null) {
                        cartFlights.add(cartFlight);
                    } else {

                        Notification.show("Error fetching flight details. Please try again.");
                    }
                }
            }

            cartGrid.setItems(cartFlights);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error refreshing cart. Please try again.");
        }
    }

    private void refreshOrdersList() {
        try {
            List<OrderDto> orders = flightClient.getUserOrders(loginView.getLoggedUserUsername());
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
            flightClient.removeFlightFromCart(flightClient.getCurrentCartId(), flightDto.getId());
            refreshCart();
            Notification.show("Flight removed from cart: " + flightDto.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error removing flight from cart. Please try again.");
        }
    }

    private void handleBuyButtonClick() {
        try {
            flightClient.createOrderFromCart();
            clearCart();
            refreshCart();
            refreshOrdersList();
            Notification.show("Purchase successful!");
            UI.getCurrent().navigate(ProfileView.class);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error processing purchase. Please try again.");
        }
    }

    private void clearCart() {
        try {
            Long cartId = flightClient.getCurrentCartId();

            if (cartId != null) {
                List<Long> flightIds = flightClient.getCartFlights(cartId);
                if (flightIds != null) {
                    for (Long flightId : flightIds) {
                        flightClient.removeFlightFromCart(cartId, flightId);
                    }
                }
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
