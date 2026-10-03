package model;

// FLIGHT.java
import java.time.LocalDateTime;

public class Flight {
    private String flightID;
    private String airlineID;
    private String planeID;
    private String routeID;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;

    public Flight(String flightID, String airlineID, String planeID, String routeID, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        this.flightID = flightID;
        this.airlineID = airlineID;
        this.planeID = planeID;
        this.routeID = routeID;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
    }

    public String getFlightID() { return flightID; }
    public void setFlightID(String flightID) { this.flightID = flightID; }

    public String getAirlineID() { return airlineID; }
    public void setAirlineID(String airlineID) { this.airlineID = airlineID; }

    public String getPlaneID() { return planeID; }
    public void setPlaneID(String planeID) { this.planeID = planeID; }

    public String getRouteID() { return routeID; }
    public void setRouteID(String routeID) { this.routeID = routeID; }

    public LocalDateTime getDepartureDateTime() { return departureDateTime; }
    public void setDepartureDateTime(LocalDateTime departureDateTime) { this.departureDateTime = departureDateTime; }

    public LocalDateTime getArrivalDateTime() { return arrivalDateTime; }
    public void setArrivalDateTime(LocalDateTime arrivalDateTime) { this.arrivalDateTime = arrivalDateTime; }
}