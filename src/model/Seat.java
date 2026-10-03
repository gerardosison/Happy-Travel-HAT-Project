package model;

// SEAT_PRICE.java
import java.math.BigDecimal;

public class Seat {
    private String flightID;
    private String seatClass;
    private BigDecimal price;
    private int availableSeats;

    public Seat(String flightID, String seatClass, BigDecimal price, int availableSeats) {
        this.flightID = flightID;
        this.seatClass = seatClass;
        this.price = price;
        this.availableSeats = availableSeats;
    }

    public String getFlightID() { return flightID; }
    public void setFlightID(String flightID) { this.flightID = flightID; }

    public String getSeatClass() { return seatClass; }
    public void setSeatClass(String seatClass) { this.seatClass = seatClass; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
