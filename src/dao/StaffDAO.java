package dao;

import model.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    private Connection conn() {
        return DBConnection.getConnection();
    }

    public Staff findByCredentials(String username, String password)
            throws SQLException {
        String sql = "SELECT * FROM STAFF WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Staff findById(int id) throws SQLException {
        String sql = "SELECT * FROM STAFF WHERE staffid = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Staff> findAll() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM STAFF ORDER BY lastname, firstname";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean insert(Staff s) throws SQLException {
        String sql = "INSERT INTO STAFF (firstname, lastname, username, password, role) " +
                     "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getUsername());
            ps.setString(4, s.getPassword());
            ps.setString(5, s.getRole().name());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Staff s) throws SQLException {
        String sql = "UPDATE STAFF SET firstname=?, lastname=?, role=? WHERE staffid=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getRole().name());
            ps.setInt   (4, s.getStaffID());
            return ps.executeUpdate() > 0;
        }
    }

    private Staff map(ResultSet rs) throws SQLException {
        return new Staff(
            rs.getInt   ("staffid"),
            rs.getString("firstname"),
            rs.getString("lastname"),
            rs.getString("username"),
            rs.getString("password"),
            Staff.Role.valueOf(rs.getString("role"))
        );
    }
}