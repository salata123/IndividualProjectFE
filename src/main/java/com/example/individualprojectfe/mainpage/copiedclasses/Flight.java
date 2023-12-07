package com.example.individualprojectfe.mainpage.copiedclasses;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "FLIGHTS")
public class Flight {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "PRICE_ID")
    private Price price;

    @Column(name = "NUM_BOOKABLE_SEATS")
    private int numberOfBookableSeats;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "FLIGHT_SEGMENTS",
            joinColumns = @JoinColumn(name = "FLIGHT_ID"),
            inverseJoinColumns = @JoinColumn(name = "SEGMENT_ID"))
    private List<Segment> segments = new ArrayList<>();
}