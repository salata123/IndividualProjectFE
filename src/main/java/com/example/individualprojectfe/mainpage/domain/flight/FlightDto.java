package com.example.individualprojectfe.mainpage.domain.flight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDto {
    private Long id;
    private Price price;
    private int numberOfBookableSeats;
    private List<Segment> segments = new ArrayList<>();
    private Long visaId;
    private String visaType;
}