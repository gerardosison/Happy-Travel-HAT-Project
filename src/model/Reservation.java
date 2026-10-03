package model;

// RESERVATION.java
import java.time.LocalDate;

public class Reservation {
    private int reservationID;
    private int passengerID;
    private String flightID;
    private String seatClass;
    private LocalDate reservationDate;
    private String transactionType;
    private String status;
    private String ticketNumber;

    public Reservation(int reservationID, int passengerID, String flightID, String seatClass, LocalDate reservationDate, String transactionType, String status, String ticketNumber) {
        this.reservationID = reservationID;
        this.passengerID = passengerID;
        this.flightID = flightID;
        this.seatClass = seatClass;
        this.reservationDate = reservationDate;
        this.transactionType = transactionType;
        this.status = status;
        this.ticketNumber = ticketNumber;
    }

    public int getReservationID() { return reservationID; }
    public void setReservationID(int reservationID) { this.reservationID = reservationID; }

    public int getPassengerID() { return passengerID; }
    public void setPassengerID(int passengerID) { this.passengerID = passengerID; }

    public String getFlightID() { return flightID; }
    public void setFlightID(String flightID) { this.flightID = flightID; }

    public String getSeatClass() { return seatClass; }
    public void setSeatClass(String seatClass) { this.seatClass = seatClass; }

    public LocalDate getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDate reservationDate) { this.reservationDate = reservationDate; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
}