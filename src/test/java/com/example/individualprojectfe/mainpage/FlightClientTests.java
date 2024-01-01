package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.FlightClient;
import com.example.individualprojectfe.mainpage.LoginView;
import com.example.individualprojectfe.mainpage.domain.flight.FlightDto;
import com.example.individualprojectfe.mainpage.domain.flight.RequestData;
import com.example.individualprojectfe.mainpage.domain.user.CartDto;
import com.example.individualprojectfe.mainpage.domain.user.OrderDto;
import com.example.individualprojectfe.mainpage.domain.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightClientTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private LoginView loginView;

    @InjectMocks
    private FlightClient flightClient;

    @BeforeEach
    void setUp() {
        flightClient = new FlightClient(restTemplate);
    }

    @Test
    void createFlights() {
        RequestData requestData = new RequestData();
        FlightDto flightDto = new FlightDto();

        // Mock the behavior of postForEntity
        when(restTemplate.postForEntity(
                eq("http://localhost:8080/v1/flights/create/CityA/CityB"),
                eq(requestData),
                eq(FlightDto.class))
        ).thenReturn(new ResponseEntity<>(flightDto, HttpStatus.OK));

        flightClient.createFlights("CityA", "CityB", requestData);

        // Verify that postForEntity was called with the expected parameters
        verify(restTemplate).postForEntity(
                eq("http://localhost:8080/v1/flights/create/CityA/CityB"),
                eq(requestData),
                eq(FlightDto.class)
        );
    }

    @Test
    void getAllFlights() {
        FlightDto[] flights = {new FlightDto(), new FlightDto()};
        when(restTemplate.getForObject(anyString(), eq(FlightDto[].class))).thenReturn(flights);

        List<FlightDto> result = flightClient.getAllFlights();

        assertEquals(2, result.size());
    }

    @Test
    void getCartFlights() {
        CartDto cartDto = new CartDto();
        cartDto.setFlightList(List.of(1L, 2L, 3L));
        when(restTemplate.getForObject(anyString(), eq(CartDto.class))).thenReturn(cartDto);

        List<Long> result = flightClient.getCartFlights(1L);

        assertEquals(List.of(1L, 2L, 3L), result);
    }

    @Test
    void removeFlightFromCart_successful() {
        ResponseEntity<CartDto> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(CartDto.class), anyLong(), anyLong())).thenReturn(responseEntity);

        flightClient.removeFlightFromCart(1L, 2L);

        // Add assertions or verifications as needed
    }

    @Test
    void addFlightToCart() {
        // Assuming your flightClient has been initialized properly, you can use the following:
        when(restTemplate.postForEntity(
                eq("http://localhost:8080/v1/carts/1/addFlight/2"),
                eq(null),
                eq(Void.class))
        ).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        flightClient.addFlightToCart(1L, 2L);

        // Now verify that the postForEntity method was called with the expected parameters
        verify(restTemplate).postForEntity(
                eq("http://localhost:8080/v1/carts/1/addFlight/2"),
                eq(null),
                eq(Void.class)
        );
    }

    @Test
    void getUserByUsername() {
        UserDto userDto = new UserDto();
        when(restTemplate.getForObject(anyString(), eq(UserDto.class), eq("testUser"))).thenReturn(userDto);

        UserDto result = flightClient.getUserByUsername("testUser");

        assertEquals(userDto, result);
    }

    @Test
    void getFlightById() {
        FlightDto flightDto = new FlightDto();
        when(restTemplate.getForObject(anyString(), eq(FlightDto.class), anyLong())).thenReturn(flightDto);

        FlightDto result = flightClient.getFlightById(1L);

        assertEquals(flightDto, result);
    }

    @Test
    void getUserOrderIds() {
        List<Long> orderIds = List.of(1L, 2L, 3L);
        ResponseEntity<List<Long>> responseEntity = new ResponseEntity<>(orderIds, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class), eq("testUser")))
                .thenReturn(responseEntity);

        List<Long> result = flightClient.getUserOrderIds("testUser");

        assertEquals(orderIds, result);
    }

    @Test
    void getUserOrders() {
        List<Long> orderIds = List.of(1L, 2L, 3L);
        ResponseEntity<List<Long>> responseEntity = new ResponseEntity<>(orderIds, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class), eq("testUser")))
                .thenReturn(responseEntity);

        List<Long> result = flightClient.getUserOrderIds("testUser");

        assertEquals(orderIds, result);
    }
}
