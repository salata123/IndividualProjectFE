package com.example.individualprojectfe.mainpage.copiedclasses;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "SEGMENTS")
public class Segment {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DEPARTURE_ID")
    private Location departure;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ARRIVAL_ID")
    private Location arrival;
}