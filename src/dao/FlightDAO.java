package dao;

import model.Flight;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    public List<Flight> getAll() {
        List<Flight> flights = new ArrayList<>();

        String sql = "SELECT * FROM FLIGHT";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                flights.add(new Flight(
                        rs.getString("flightid"),
                        rs.getString("airlineid"),
                        rs.getString("planeid"),
                        rs.getString("routeid"),
                        rs.getTimestamp("departuredatetime").toLocalDateTime(),
                        rs.getTimestamp("arrivaldatetime").toLocalDateTime()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flights;
    }

    public void add(Flight flight) {
        String sql = "INSERT INTO FLIGHT VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, flight.getFlightID());
            ps.setString(2, flight.getAirlineID());
            ps.setString(3, flight.getPlaneID());
            ps.setString(4, flight.getRouteID());
            ps.setTimestamp(5, Timestamp.valueOf(flight.getDepartureDateTime()));
            ps.setTimestamp(6, Timestamp.valueOf(flight.getArrivalDateTime()));

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Flight flight) {
        String sql = "UPDATE FLIGHT SET airlineid=?, planeid=?, routeid=?, departuredatetime=?, arrivaldatetime=? WHERE flightid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, flight.getAirlineID());
            ps.setString(2, flight.getPlaneID());
            ps.setString(3, flight.getRouteID());
            ps.setTimestamp(4, Timestamp.valueOf(flight.getDepartureDateTime()));
            ps.setTimestamp(5, Timestamp.valueOf(flight.getArrivalDateTime()));
            ps.setString(6, flight.getFlightID());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String flightID) {
        String sql = "DELETE FROM FLIGHT WHERE flightid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, flightID);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}