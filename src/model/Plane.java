package model;

// PLANE.java
public class Plane {
    private String planeID;
    private String airlineID;
    private String planeModel;
    private int totalSeats;
    private int firstClassSeats;
    private int economySeats;
    private String amenities;

    public Plane(String planeID, String airlineID, String planeModel, int totalSeats, int firstClassSeats, int economySeats, String amenties) {
        this.planeID = planeID;
        this.airlineID = airlineID;
        this.planeModel = planeModel;
        this.totalSeats = totalSeats;
        this.firstClassSeats = firstClassSeats;
        this.economySeats = economySeats;
        this.amenities = amenties;
    }

    public String getPlaneID() { return planeID; }
    public void setPlaneID(String planeID) { this.planeID = planeID; }

    public String getAirlineID() { return airlineID; }
    public void setAirlineID(String airlineID) { this.airlineID = airlineID; }

    public String getPlaneModel() { return planeModel; }
    public void setPlaneModel(String planeModel) { this.planeModel = planeModel; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getFirstClassSeats() { return firstClassSeats; }
    public void setFirstClassSeats(int firstClassSeats) { this.firstClassSeats = firstClassSeats; }

    public int getEconomySeats() { return economySeats; }
    public void setEconomySeats(int economySeats) { this.economySeats = economySeats; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
}