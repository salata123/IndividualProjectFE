package com.example.individualprojectfe.mainpage.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "CARTS")
public class Cart {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private User user;

    @ElementCollection
    @CollectionTable(
            name = "CART_FLIGHTS",
            joinColumns = @JoinColumn(name = "CART_ID")
    )
    @Column(name = "FLIGHT_ID")
    private List<Long> flightList;
}
