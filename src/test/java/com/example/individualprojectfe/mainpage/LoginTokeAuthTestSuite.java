package com.example.individualprojectfe.mainpage;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

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
        //Given
        String username = "testUser";
        Boolean mockResponse = true;

        //When
        when(restTemplate.getForObject(
                ArgumentMatchers.eq("http://localhost:8080/v1/loginTokens/checkExpiration/testUser"),
                ArgumentMatchers.eq(Boolean.class)
        )).thenReturn(mockResponse);

        boolean result = loginTokenAuth.isTokenExpired(username);

        //Then
        assertTrue(result);
    }
}
