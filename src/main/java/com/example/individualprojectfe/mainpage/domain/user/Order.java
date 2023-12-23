package com.example.individualprojectfe.mainpage.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "CART_ID")
    private Long cartId;

    @Column(name = "USER_ID")
    private Long userId;

    @ElementCollection
    @CollectionTable(
            name = "ORDER_FLIGHTS",
            joinColumns = @JoinColumn(name = "ORDER_ID")
    )
    @Column(name = "FLIGHT_ID_LIST")
    private List<Long> flights;
}
