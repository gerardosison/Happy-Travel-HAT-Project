// ReservationDAO.java
package dao;

import model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();

        String sql = "SELECT * FROM RESERVATION";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reservations.add(new Reservation(
                        rs.getInt("reservationid"),
                        rs.getInt("passengerid"),
                        rs.getString("flightid"),
                        rs.getString("seatclass"),
                        rs.getDate("reservationdate").toLocalDate(),
                        rs.getString("transactiontype"),
                        rs.getString("status"),
                        rs.getString("ticketnumber")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public void add(Reservation reservation) {
        String sql = "INSERT INTO RESERVATION VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservation.getReservationID());
            ps.setInt(2, reservation.getPassengerID());
            ps.setString(3, reservation.getFlightID());
            ps.setString(4, reservation.getSeatClass());
            ps.setDate(5, Date.valueOf(reservation.getReservationDate()));
            ps.setString(6, reservation.getTransactionType());
            ps.setString(7, reservation.getStatus());
            ps.setString(8, reservation.getTicketNumber());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Reservation reservation) {
        String sql = "UPDATE RESERVATION SET customerid=?, flightid=?, seatclass=?, reservationdate=?, transactiontype=?, status=?, ticketnumber=? WHERE reservationid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservation.getPassengerID());
            ps.setString(2, reservation.getFlightID());
            ps.setString(3, reservation.getSeatClass());
            ps.setDate(4, Date.valueOf(reservation.getReservationDate()));
            ps.setString(5, reservation.getTransactionType());
            ps.setString(6, reservation.getStatus());
            ps.setString(7, reservation.getTicketNumber());
            ps.setInt(8, reservation.getReservationID());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int reservationID) {
        String sql = "DELETE FROM RESERVATION WHERE reservationid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationID);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}