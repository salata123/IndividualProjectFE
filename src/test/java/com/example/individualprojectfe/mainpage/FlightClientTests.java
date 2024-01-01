package com.example.individualprojectfe.mainpage;

import com.example.individualprojectfe.mainpage.domain.client.CartService;
import com.example.individualprojectfe.mainpage.domain.client.FlightService;
import com.example.individualprojectfe.mainpage.domain.client.OrderService;
import com.example.individualprojectfe.mainpage.domain.client.UserService;
import com.example.individualprojectfe.mainpage.domain.flight.FlightDto;
import com.example.individualprojectfe.mainpage.domain.flight.RequestData;
import com.example.individualprojectfe.mainpage.domain.user.CartDto;
import com.example.individualprojectfe.mainpage.domain.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

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
    private CartService cartService;
    @InjectMocks
    private UserService userService;
    @Mock
    private OrderService orderService;

    @InjectMocks
    private FlightService flightService;

    @BeforeEach
    void setUp() {
        flightService = new FlightService(restTemplate);
    }

    @Test
    void createFlights() {
        RequestData requestData = new RequestData();
        FlightDto flightDto = new FlightDto();

        when(restTemplate.postForEntity(
                eq("http://localhost:8080/v1/flights/create/CityA/CityB"),
                eq(requestData),
                eq(FlightDto.class))
        ).thenReturn(new ResponseEntity<>(flightDto, HttpStatus.OK));

        flightService.createFlights("CityA", "CityB", requestData);

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

        List<FlightDto> result = flightService.getAllFlights();

        assertEquals(2, result.size());
    }

    @Test
    void getCartFlights() {
        CartDto cartDto = new CartDto();
        cartDto.setFlightList(List.of(1L, 2L, 3L));

        when(loginView.getLoggedUserUsername()).thenReturn("testUser");

        when(restTemplate.getForObject(anyString(), eq(CartDto.class))).thenReturn(cartDto);

        List<Long> result = cartService.getCartFlights(1L);

        assertEquals(List.of(1L, 2L, 3L), result);

        verify(loginView).getLoggedUserUsername();
    }

    @Test
    void removeFlightFromCart_successful() {
        ResponseEntity<CartDto> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(CartDto.class), anyLong(), anyLong())).thenReturn(responseEntity);

        cartService.removeFlightFromCart(1L, 2L);

        // TODO: ADD ASSERTIONS
    }

    @Test
    void addFlightToCart() {
        when(restTemplate.postForEntity(
                eq("http://localhost:8080/v1/carts/1/addFlight/2"),
                eq(null),
                eq(Void.class))
        ).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        cartService.addFlightToCart(1L, 2L);

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

        UserDto result = userService.getUserByUsername("testUser");

        assertEquals(userDto, result);
    }

    @Test
    void getFlightById() {
        FlightDto flightDto = new FlightDto();
        when(restTemplate.getForObject(anyString(), eq(FlightDto.class), anyLong())).thenReturn(flightDto);

        FlightDto result = flightService.getFlightById(1L);

        assertEquals(flightDto, result);
    }

    @Test
    void getUserOrderIds() {
        List<Long> orderIds = List.of(1L, 2L, 3L);
        ResponseEntity<List<Long>> responseEntity = new ResponseEntity<>(orderIds, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class), eq("testUser")))
                .thenReturn(responseEntity);

        List<Long> result = userService.getUserOrderIds("testUser");

        assertEquals(orderIds, result);
    }

    @Test
    void getUserOrders() {
        List<Long> orderIds = List.of(1L, 2L, 3L);
        ResponseEntity<List<Long>> responseEntity = new ResponseEntity<>(orderIds, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class), eq("testUser")))
                .thenReturn(responseEntity);

        List<Long> result = userService.getUserOrderIds("testUser");

        assertEquals(orderIds, result);
    }
}
