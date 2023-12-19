package com.example.individualprojectfe.mainpage.domain.flight;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "PRICES")
public class Price {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "TOTAL")
    private String total;

    @Column(name = "BASE")
    private String base;
}