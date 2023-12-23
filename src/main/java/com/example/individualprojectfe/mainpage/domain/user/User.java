package com.example.individualprojectfe.mainpage.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "USERS")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "CART_ID")
    private Long cartId;

    @ElementCollection
    @CollectionTable(
            name = "USER_ORDERS",
            joinColumns = @JoinColumn(name = "USER_ID")
    )
    @Column(name = "ORDER_IDS")
    private List<Long> orders;

    @Column(name = "LOGIN_TOKEN_ID")
    private Long loginTokenId;
}
