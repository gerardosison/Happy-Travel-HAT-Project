// SeatDAO.java
package dao;

import model.Seat;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    public List<Seat> getAll() {
        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT * FROM SEAT";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                seats.add(new Seat(
                        rs.getString("flightid"),
                        rs.getString("seatclass"),
                        rs.getBigDecimal("price"),
                        rs.getInt("availableseats")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seats;
    }

    public void add(Seat seat) {
        String sql = "INSERT INTO SEAT VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, seat.getFlightID());
            ps.setString(2, seat.getSeatClass());
            ps.setBigDecimal(3, seat.getPrice());
            ps.setInt(4, seat.getAvailableSeats());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Seat seat) {
        String sql = "UPDATE SEAT SET price=?, availableseats=? WHERE flightid=? AND seatclass=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, seat.getPrice());
            ps.setInt(2, seat.getAvailableSeats());
            ps.setString(3, seat.getFlightID());
            ps.setString(4, seat.getSeatClass());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String flightID, String seatClass) {
        String sql = "DELETE FROM SEAT WHERE flightid=? AND seatclass=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, flightID);
            ps.setString(2, seatClass);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePriceOnly(String flightID, String seatClass, BigDecimal newPrice) throws SQLException {
        String sql = "UPDATE seat SET price = ? WHERE flight_id = ? AND seat_class = ?";

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, newPrice);
            pstmt.setString(2, flightID);
            pstmt.setString(3, seatClass);

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating price failed: No matching seat record found for Flight " 
                        + flightID + " (" + seatClass + ").");
            }
        }
    }
}