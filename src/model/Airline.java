package model;

// AIRLINE.java
public class Airline {
    private String airlineID;
    private String airlineName;
    private String contactInfo;
    private String headquartersLocation;

    public Airline(String airlineID, String airlineName, String contactInfo, String headquartersLocation) {
        this.airlineID = airlineID;
        this.airlineName = airlineName;
        this.contactInfo = contactInfo;
        this.headquartersLocation = headquartersLocation;
    }

    public String getAirlineID() { return airlineID; }
    public void setAirlineID(String airlineID) { this.airlineID = airlineID; }

    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getHeadquartersLocation() { return headquartersLocation; }
    public void setHeadquartersLocation(String headquartersLocation) { this.headquartersLocation = headquartersLocation; }
}