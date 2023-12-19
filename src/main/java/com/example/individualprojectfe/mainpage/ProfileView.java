package com.example.individualprojectfe.mainpage;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("profile")
@SpringComponent
public class ProfileView extends VerticalLayout {
    public ProfileView() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // User is logged in
            add(new H3("Welcome, " + authentication.getName()));
            Button logoutButton = new Button("Logout");
            logoutButton.addClickListener(e -> {
                SecurityContextHolder.clearContext();
                getUI().ifPresent(ui -> ui.navigate("login"));
            });
            add(logoutButton);
        }

        // Add a header to your profile view
        add(new H3("Profile Page"));

        // Create a button to search flights
        Button searchFlightsButton = new Button("Search Flights");

        // Set the button click listener to navigate to FlightView.class
        searchFlightsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(FlightView.class)));

        // Create a horizontal layout to display cart and orders lists next to each other
        HorizontalLayout listsLayout = new HorizontalLayout();

        // Create and add the cart list (you can replace this with your actual cart implementation)
        VerticalLayout cartList = new VerticalLayout();
        cartList.add(new H3("Cart"));
        // Add items to the cart list or integrate your cart logic here
        listsLayout.add(cartList);

        // Create and add the orders list (you can replace this with your actual orders implementation)
        VerticalLayout ordersList = new VerticalLayout();
        ordersList.add(new H3("Orders"));
        // Add items to the orders list or integrate your orders logic here
        listsLayout.add(ordersList);

        // Add the searchFlightsButton and listsLayout to the main layout
        add(searchFlightsButton, listsLayout);
    }
}
