package ui;

import model.Passenger;
import model.Staff;

public final class Session {

    public enum UserType { PASSENGER, STAFF }

    private static Session instance;

    private Passenger passenger;
    private Staff     staff;
    private UserType  userType;

    private Session() {}

    public static synchronized Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public void loginAsPassenger(Passenger p) {
        this.passenger = p;
        this.staff     = null;
        this.userType  = UserType.PASSENGER;
    }

    public void loginAsStaff(Staff s) {
        this.staff     = s;
        this.passenger = null;
        this.userType  = UserType.STAFF;
    }

    public void logout() {
        passenger = null;
        staff     = null;
        userType  = null;
    }

    public boolean isLoggedIn()       { return userType != null; }
    public boolean isPassenger()      { return userType == UserType.PASSENGER; }
    public boolean isStaff()          { return userType == UserType.STAFF; }
    public boolean isAdmin()          { return isStaff() && staff.isAdmin(); }

    public Passenger getPassenger()   { return passenger; }
    public Staff     getStaff()       { return staff; }
    public UserType  getUserType()    { return userType; }

    public String getDisplayName() {
        if (isPassenger()) return passenger.getFullName();
        if (isStaff())     return staff.getFullName() + " [" + staff.getRole() + "]";
        return "Guest";
    }
}