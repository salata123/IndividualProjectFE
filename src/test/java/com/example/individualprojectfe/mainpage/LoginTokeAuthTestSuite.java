package com.example.individualprojectfe.mainpage;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

@SpringBootTest
public class LoginTokeAuthTestSuite {

    @InjectMocks
    private LoginTokenAuth loginTokenAuth;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplate mockRestTemplate;

    @Test
    void testIsTokenExpired(){
        // Arrange
        String username = "testUser";
        Boolean mockResponse = true;

        // Mocking the RestTemplate behavior
        when(restTemplate.getForObject(
                ArgumentMatchers.eq("http://localhost:8080/v1/loginTokens/checkExpiration/testUser"),
                ArgumentMatchers.eq(Boolean.class)
        )).thenReturn(mockResponse);

        // Act
        boolean result = loginTokenAuth.isTokenExpired(username);
        System.out.println(result);

        // Assert
        assertTrue(result);
    }
}
