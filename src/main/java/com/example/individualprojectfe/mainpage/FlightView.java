package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.copiedclasses.FlightDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("flights")
public class FlightView extends VerticalLayout {

    private final FlightClient flightClient;

    private final Grid<FlightDto> flightGrid;


    public FlightView(FlightClient flightClient) {
        this.flightClient = flightClient;

        Button getFlightsButton = new Button("Get Flights", event -> refreshFlights());

        Button createFlightsButton = new Button("Create Flights", event -> createFlights());

        flightGrid = new Grid<>(FlightDto.class);

        flightGrid.setColumns("id", "numberOfBookableSeats", "segments");
        flightGrid.getColumnByKey("numberOfBookableSeats").setHeader("Seats available");

        flightGrid.addColumn(FlightDto::getPrice)
                .setHeader("Price")
                .setRenderer(new TextRenderer<>(priceDto ->
                        String.format("%s %s", priceDto.getPrice().getTotal(), priceDto.getPrice().getCurrency())));

        add(getFlightsButton, createFlightsButton, flightGrid);
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

    private void createFlights() {
        try {
            flightClient.createFlights();
            refreshFlights();
            Notification.show("Flights created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error creating flights. Please try again.");
        }
    }
}