package model;

// ROUTE.java
public class Route {
    private String routeID;
    private String origin;
    private String destination;
    private String estimatedDuration;

    public Route(String routeID, String origin, String destination, String estimatedDuration) {
        this.routeID = routeID;
        this.origin = origin;
        this.destination = destination;
        this.estimatedDuration = estimatedDuration;
    }

    public String getRouteID() { return routeID; }
    public void setRouteID(String routeID) { this.routeID = routeID; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }
}