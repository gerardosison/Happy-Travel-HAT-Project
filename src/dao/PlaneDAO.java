// PlaneDAO.java
package dao;

import model.Plane;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaneDAO {

    public List<Plane> getAll() {
        List<Plane> planes = new ArrayList<>();

        String sql = "SELECT * FROM PLANE";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                planes.add(new Plane(
                        rs.getString("planeid"),
                        rs.getString("airlineid"),
                        rs.getString("planemodel"),
                        rs.getInt("totalseats"),
                        rs.getInt("firstclassseats"),
                        rs.getInt("economyseats"),
                        rs.getString("amenities")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return planes;
    }

    public void add(Plane plane) {
        String sql = "INSERT INTO PLANE VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, plane.getPlaneID());
            ps.setString(2, plane.getAirlineID());
            ps.setString(3, plane.getPlaneModel());
            ps.setInt(4, plane.getTotalSeats());
            ps.setInt(5, plane.getFirstClassSeats());
            ps.setInt(6, plane.getEconomySeats());
            ps.setString(7, plane.getAmenities());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Plane plane) {
        String sql = "UPDATE PLANE SET airlineid=?, planemodel=?, totalseats=?, firstclassseats=?, economyseats=?, amenities=? WHERE planeid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, plane.getAirlineID());
            ps.setString(2, plane.getPlaneModel());
            ps.setInt(3, plane.getTotalSeats());
            ps.setInt(4, plane.getFirstClassSeats());
            ps.setInt(5, plane.getEconomySeats());
            ps.setString(6, plane.getAmenities());
            ps.setString(7, plane.getPlaneID());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String planeID) {
        String sql = "DELETE FROM PLANE WHERE planeid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, planeID);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}