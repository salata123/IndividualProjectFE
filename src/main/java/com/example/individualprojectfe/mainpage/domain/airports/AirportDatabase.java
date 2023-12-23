package com.example.individualprojectfe.mainpage.domain.airports;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AirportDatabase {
    private List<Airport> airports;

    public AirportDatabase() {
        this.airports = createAirports();
    }
    public static List<Airport> createAirports() {
        List<Airport> airports = new ArrayList<>();
        airports.add(new Airport("Heathrow Airport", "United Kingdom", "GB", "LHR"));
        airports.add(new Airport("John F. Kennedy International Airport", "United States", "US", "JFK"));
        airports.add(new Airport("Tokyo Haneda Airport", "Japan", "JP", "HND"));
        airports.add(new Airport("Beijing Capital International Airport", "China", "CN", "PEK"));
        airports.add(new Airport("Dubai International Airport", "United Arab Emirates", "AE", "DXB"));
        airports.add(new Airport("Frankfurt Airport", "Germany", "DE", "FRA"));
        airports.add(new Airport("Singapore Changi Airport", "Singapore", "SG", "SIN"));
        airports.add(new Airport("Incheon International Airport", "South Korea", "KR", "ICN"));
        airports.add(new Airport("Los Angeles International Airport", "United States", "US", "LAX"));
        airports.add(new Airport("O'Hare International Airport", "United States", "US", "ORD"));
        airports.add(new Airport("Amsterdam Airport Schiphol", "Netherlands", "NL", "AMS"));
        airports.add(new Airport("Denver International Airport", "United States", "US", "DEN"));
        airports.add(new Airport("Suvarnabhumi Airport", "Thailand", "TH", "BKK"));
        airports.add(new Airport("Hartsfield-Jackson Atlanta International Airport", "United States", "US", "ATL"));
        airports.add(new Airport("Indira Gandhi International Airport", "India", "IN", "DEL"));
        airports.add(new Airport("Sydney Kingsford Smith Airport", "Australia", "AU", "SYD"));
        airports.add(new Airport("Munich Airport", "Germany", "DE", "MUC"));
        airports.add(new Airport("Barcelona-El Prat Airport", "Spain", "ES", "BCN"));
        airports.add(new Airport("Dallas/Fort Worth International Airport", "United States", "US", "DFW"));
        airports.add(new Airport("Hong Kong International Airport", "Hong Kong", "HK", "HKG"));
        airports.add(new Airport("Rome Leonardo da Vinci-Fiumicino Airport", "Italy", "IT", "FCO"));
        airports.add(new Airport("Toronto Pearson International Airport", "Canada", "CA", "YYZ"));
        airports.add(new Airport("Istanbul Airport", "Turkey", "TR", "IST"));
        airports.add(new Airport("Zurich Airport", "Switzerland", "CH", "ZRH"));
        airports.add(new Airport("Vienna International Airport", "Austria", "AT", "VIE"));
        airports.add(new Airport("San Francisco International Airport", "United States", "US", "SFO"));
        airports.add(new Airport("Incheon International Airport", "South Korea", "KR", "ICN"));
        airports.add(new Airport("Changi Airport", "Singapore", "SG", "SIN"));
        airports.add(new Airport("Jomo Kenyatta International Airport", "Kenya", "KE", "NBO"));
        airports.add(new Airport("Marrakech Menara Airport", "Morocco", "MA", "RAK"));
        airports.add(new Airport("Stockholm Arlanda Airport", "Sweden", "SE", "ARN"));
        airports.add(new Airport("Doha Hamad International Airport", "Qatar", "QA", "DOH"));
        return airports;
    }

    public List<String> getIataCodes() {
        List<String> iataCodes = new ArrayList<>();
        for (Airport airport : airports) {
            iataCodes.add(airport.getIataCode());
        }
        return iataCodes;
    }

    public List<String> getAirportNames() {
        List<String> airportNames = new ArrayList<>();
        for (Airport airport : airports) {
            airportNames.add(airport.getName());
        }
        return airportNames;
    }

    public String getIataCodeByName(String airportName) {
        for (Airport airport : airports) {
            if (airport.getName().equalsIgnoreCase(airportName)) {
                return airport.getIataCode();
            }
        }
        return null; // Return null if the airport name is not found
    }
}
