// RouteDAO.java
package dao;

import model.Route;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RouteDAO {

    public List<Route> getAll() {
        List<Route> routes = new ArrayList<>();

        String sql = "SELECT * FROM ROUTE";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                routes.add(new Route(
                        rs.getString("routeid"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("estimatedduration")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return routes;
    }

    public void add(Route route) {
        String sql = "INSERT INTO ROUTE VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, route.getRouteID());
            ps.setString(2, route.getOrigin());
            ps.setString(3, route.getDestination());
            ps.setString(4, route.getEstimatedDuration());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Route route) {
        String sql = "UPDATE ROUTE SET origin=?, destination=?, estimatedduration=? WHERE routeid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, route.getOrigin());
            ps.setString(2, route.getDestination());
            ps.setString(3, route.getEstimatedDuration());
            ps.setString(4, route.getRouteID());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String routeID) {
        String sql = "DELETE FROM ROUTE WHERE routeid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, routeID);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}