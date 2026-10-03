package dao;

import model.Airline;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirlineDAO {

    public List<Airline> getAll() {
        List<Airline> airlines = new ArrayList<>();

        String sql = "SELECT * FROM AIRLINE";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                airlines.add(new Airline(
                        rs.getString("airlineid"),
                        rs.getString("airlinename"),
                        rs.getString("contactinfo"),
                        rs.getString("headquarterslocation")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return airlines;
    }

    public void add(Airline airline) {
        String sql = "INSERT INTO AIRLINE VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, airline.getAirlineID());
            ps.setString(2, airline.getAirlineName());
            ps.setString(3, airline.getContactInfo());
            ps.setString(4, airline.getHeadquartersLocation());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Airline airline) {
        String sql = "UPDATE AIRLINE SET airlinename=?, contactinfo=?, headquarterslocation=? WHERE airlineid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, airline.getAirlineName());
            ps.setString(2, airline.getContactInfo());
            ps.setString(3, airline.getHeadquartersLocation());
            ps.setString(4, airline.getAirlineID());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String airlineID) {
        String sql = "DELETE FROM AIRLINE WHERE airlineid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, airlineID);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}