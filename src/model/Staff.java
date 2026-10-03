package model;

public class Staff {
    public enum Role { Staff, Admin }

    private int    staffID;
    private String firstName;
    private String lastName;
    private String username;
    private String password;   // stored as hash in DB
    private Role   role;

    public Staff() {}

    public Staff(int staffID, String firstName, String lastName,
                 String username, String password, Role role) {
        this.staffID   = staffID;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.username  = username;
        this.password  = password;
        this.role      = role;
    }

    public int    getStaffID()   { return staffID; }
    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public Role   getRole()      { return role; }

    public void setStaffID(int staffID)       { this.staffID   = staffID; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName  = lastName; }
    public void setUsername(String username)   { this.username  = username; }
    public void setPassword(String password)   { this.password  = password; }
    public void setRole(Role role)             { this.role      = role; }

    public String getFullName() { return firstName + " " + lastName; }
    public boolean isAdmin()    { return role == Role.Admin; }

    @Override
    public String toString() { return getFullName() + " [" + role + "]"; }
}