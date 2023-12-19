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
@Entity(name = "LOCATIONS")
public class Location {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "IATA_CODE")
    private String iataCode;

    @Column(name = "TERMINAL")
    private String terminal;

    @Column(name = "AT")
    private String at;
}