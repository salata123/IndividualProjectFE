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

    @ManyToOne
    @JoinColumn(name = "CART_ID")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ElementCollection
    @CollectionTable(
            name = "ORDER_FLIGHTS",
            joinColumns = @JoinColumn(name = "ORDER_ID")
    )
    @Column(name = "FLIGHT_ID")
    private List<Long> flights;
}
