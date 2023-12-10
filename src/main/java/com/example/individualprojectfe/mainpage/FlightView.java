package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.domain.flight.FlightDto;
import com.example.individualprojectfe.mainpage.domain.flight.RequestData;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("flights")
public class FlightView extends VerticalLayout {

    private final FlightClient flightClient;
    private final Grid<FlightDto> flightGrid;
    private TextField currencyCodeField;
    private TextField originLocationCodeField;
    private TextField destinationLocationCodeField;
    private DatePicker departureDateField;
    private TimePicker departureTimeField;


    public FlightView(FlightClient flightClient) {
        this.flightClient = flightClient;

        Button createFlightsButton = new Button("Create Flights", event -> createFlights());
        Button loginButton = new Button("Login", event -> navigateToLogin());
        loginButton.getElement().getThemeList().add("primary");

        flightGrid = new Grid<>(FlightDto.class);
        currencyCodeField = new TextField("Currency Code");
        originLocationCodeField = new TextField("Origin Location Code");
        destinationLocationCodeField = new TextField("Destination Location Code");
        departureDateField = new DatePicker("Departure Date");
        departureTimeField = new TimePicker("Departure Time");

        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setJustifyContentMode(JustifyContentMode.END);
        topBar.add(loginButton);

        flightGrid.setColumns("id", "numberOfBookableSeats");
        flightGrid.getColumnByKey("numberOfBookableSeats").setHeader("Seats available");

        flightGrid.addColumn(flightDto -> flightDto.getSegments().size())
                .setHeader("Segments");

        flightGrid.addColumn(FlightDto::getPrice)
                .setHeader("Price")
                .setRenderer(new TextRenderer<>(priceDto ->
                        String.format("%s %s", priceDto.getPrice().getTotal(), priceDto.getPrice().getCurrency())));

        flightGrid.setWidth("50%");
        setSizeFull();
        setHorizontalComponentAlignment(Alignment.CENTER, flightGrid);
        setSpacing(true);

        HorizontalLayout inputLayout = new HorizontalLayout(currencyCodeField, originLocationCodeField, destinationLocationCodeField, departureDateField, departureTimeField, createFlightsButton);
        inputLayout.setSpacing(true);
        setHorizontalComponentAlignment(Alignment.CENTER, createFlightsButton, inputLayout, flightGrid);

        add(topBar, inputLayout, createFlightsButton, flightGrid);
        expand(flightGrid);
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
            String currencyCode = currencyCodeField.getValue();
            String originLocationCode = originLocationCodeField.getValue();
            String destinationLocationCode = destinationLocationCodeField.getValue();
            String departureDate = departureDateField.getValue().toString();
            String departureTime = departureTimeField.getValue().toString() + ":00";

            RequestData requestData = new RequestData();
            requestData.setCurrencyCode(currencyCode);
            requestData.setOriginLocationCode(originLocationCode);
            requestData.setDestinationLocationCode(destinationLocationCode);
            requestData.setDepartureDate(departureDate);
            requestData.setDepartureTime(departureTime);

            flightClient.createFlights(requestData);
            refreshFlights();
            Notification.show("Flights created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error creating flights. Please try again.");
        }
    }

    private void navigateToLogin() {
        getUI().ifPresent(ui -> ui.navigate(LoginView.class));
    }
}