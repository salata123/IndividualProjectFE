package com.example.individualprojectfe.mainpage.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private Long cartId;
    private List<Long> orders;
    private Long loginTokenId;
}