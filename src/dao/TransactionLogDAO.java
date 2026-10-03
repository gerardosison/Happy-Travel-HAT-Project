// TransactionLogDAO.java
package dao;

import model.TransactionLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionLogDAO {

    public List<TransactionLog> getAll() {
        List<TransactionLog> logs = new ArrayList<>();

        String sql = "SELECT * FROM TRANSACTION_LOG";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(new TransactionLog(
                        rs.getInt("transactionid"),
                        rs.getInt("reservationid"),
                        rs.getInt("passengerid"),
                        rs.getInt("staffid"),
                        rs.getString("transactiontype"),
                        rs.getDate("transactiondate").toLocalDate(),
                        rs.getTime("transactiontime").toLocalTime(),
                        rs.getString("channel")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    public void add(TransactionLog log) {
        if (log.getStaffID() > 0) {
            String sql = "INSERT INTO TRANSACTION_LOG VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, log.getTransactionID());
                ps.setInt(2, log.getReservationID());
                ps.setInt(3, log.getPassengerID());
                ps.setInt(4, log.getStaffID());
                ps.setString(5, log.getTransactionType());
                ps.setDate(6, Date.valueOf(log.getTransactionDate()));
                ps.setTime(7, Time.valueOf(log.getTransactionTime()));
                ps.setString(8, log.getChannel());

                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String sql = "INSERT INTO TRANSACTION_LOG (transactionid, reservationid, passengerid, transactiontype, transactiondate, transactiontime, channel) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, log.getTransactionID());
                ps.setInt(2, log.getReservationID());
                ps.setInt(3, log.getPassengerID());
                ps.setString(4, log.getTransactionType());
                ps.setDate(5, Date.valueOf(log.getTransactionDate()));
                ps.setTime(6, Time.valueOf(log.getTransactionTime()));
                ps.setString(7, log.getChannel());

                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
    }

    public void update(TransactionLog log) {
        String sql = "UPDATE TRANSACTION_LOG SET reservationid=?, passengerid=?, staffid=?, transactiontype=?, transactiondate=?, transactiontime=?, channel=? WHERE transactionid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, log.getReservationID());
            ps.setInt(2, log.getPassengerID());
            ps.setInt(3, log.getStaffID());
            ps.setString(4, log.getTransactionType());
            ps.setDate(5, Date.valueOf(log.getTransactionDate()));
            ps.setTime(6, Time.valueOf(log.getTransactionTime()));
            ps.setString(7, log.getChannel());
            ps.setInt(8, log.getTransactionID());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int transactionID) {
        String sql = "DELETE FROM TRANSACTION_LOG WHERE transactionid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transactionID);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}