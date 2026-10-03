package dao;

import model.Passenger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerDAO {

    public List<Passenger> getAll() {

        List<Passenger> passengers = new ArrayList<>();

        String sql = "SELECT * FROM PASSENGER";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                passengers.add(new Passenger(
                        rs.getInt("passengerid"),
                        rs.getString("lastname"),
                        rs.getString("firstname"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getDate("registrationdate").toLocalDate(),
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return passengers;
    }

    public boolean add(Passenger passenger) {

        String sql = """
                INSERT INTO PASSENGER
                (lastname, firstname, email, phone, address,
                 registrationdate, username, password)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, passenger.getLastName());
            ps.setString(2, passenger.getFirstName());
            ps.setString(3, passenger.getEmail());
            ps.setString(4, passenger.getPhone());
            ps.setString(5, passenger.getAddress());
            ps.setDate(6, Date.valueOf(passenger.getRegistrationDate()));
            ps.setString(7, passenger.getUsername());
            ps.setString(8, passenger.getPassword());

            int rowsAffected = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    passenger.setPassengerID(rs.getInt(1));
                }
            }

            return rowsAffected > 0;

        } catch (SQLException e) {

            System.err.println("[PassengerDAO] INSERT FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void update(Passenger passenger) {

        String sql = """
                UPDATE PASSENGER
                SET lastname=?,
                    firstname=?,
                    email=?,
                    phone=?,
                    address=?,
                    registrationdate=?,
                    username=?,
                    password=?
                WHERE passengerid=?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, passenger.getLastName());
            ps.setString(2, passenger.getFirstName());
            ps.setString(3, passenger.getEmail());
            ps.setString(4, passenger.getPhone());
            ps.setString(5, passenger.getAddress());
            ps.setDate(6, Date.valueOf(passenger.getRegistrationDate()));
            ps.setString(7, passenger.getUsername());
            ps.setString(8, passenger.getPassword());
            ps.setInt(9, passenger.getPassengerID());

            ps.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void delete(int passengerID) {

        String sql = "DELETE FROM PASSENGER WHERE passengerid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, passengerID);

            ps.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public Passenger findByCredentials(String username, String password) {

        String sql = """
                SELECT * FROM PASSENGER
                WHERE username = ? AND password = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Passenger(
                            rs.getInt("passengerid"),
                            rs.getString("lastname"),
                            rs.getString("firstname"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getDate("registrationdate").toLocalDate(),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }
}