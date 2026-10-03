package model;

// TRANSACTION_LOG.java
import java.time.LocalDate;
import java.time.LocalTime;

public class TransactionLog {
    private int transactionID;
    private int reservationID;
    private int passengerID;
    private int staffID;
    private String transactionType;
    private LocalDate transactionDate;
    private LocalTime transactionTime;
    private String channel;

    public TransactionLog(int transactionID, int reservationID, int passengerID, int  staffID, String transactionType, LocalDate transactionDate, LocalTime transactionTime, String channel) {
        this.transactionID = transactionID;
        this.reservationID = reservationID;
        this.passengerID = passengerID;
        this.staffID =  staffID;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.channel = channel;
    }

    public int getTransactionID() { return transactionID; }
    public void setTransactionID(int transactionID) { this.transactionID = transactionID; }

    public int getReservationID() { return reservationID; }
    public void setReservationID(int reservationID) { this.reservationID = reservationID; }

    public int getPassengerID() { return passengerID; }
    public void setPassengerID(int passengerID) { this.passengerID = passengerID; }

    public int getStaffID() { return  staffID; }
    public void setStaffID(int staffID) { this.staffID =  staffID; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public LocalTime getTransactionTime() { return transactionTime; }
    public void setTransactionTime(LocalTime transactionTime) { this.transactionTime = transactionTime; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}