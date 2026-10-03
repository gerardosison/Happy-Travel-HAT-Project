package model;

import java.time.LocalDate;

public class Passenger {

    private int passengerID;
    private String lastName;
    private String firstName;
    private String email;
    private String phone;
    private String address;
    private LocalDate registrationDate;
    private String username;
    private String password;

    public Passenger(int passengerID,
                     String lastName,
                     String firstName,
                     String email,
                     String phone,
                     String address,
                     LocalDate registrationDate,
                     String username,
                     String password) {

        this.passengerID = passengerID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
        this.username = username;
        this.password = password;
    }

    public int getPassengerID() {
        return passengerID;
    }

    public void setPassengerID(int passengerID) {
        this.passengerID = passengerID;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}