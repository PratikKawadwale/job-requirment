package com.recruitment.dao;

import com.recruitment.model.Location;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for locations master table in rpms_db.
 */
public class LocationDAO {

    public int insert(Location loc) throws SQLException {
        String sql = "INSERT INTO locations (city_name, state_name, country_name, is_active) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, loc.getCityName());
            stmt.setString(2, loc.getStateName());
            stmt.setString(3, loc.getCountryName() != null ? loc.getCountryName() : "India");
            stmt.setBoolean(4, loc.isActive());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        loc.setLocationId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public List<Location> findAll() throws SQLException {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT * FROM locations ORDER BY city_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToLocation(rs));
            }
        }
        return list;
    }

    public List<Location> findActive() throws SQLException {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT * FROM locations WHERE is_active = 1 ORDER BY city_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToLocation(rs));
            }
        }
        return list;
    }

    private Location mapResultSetToLocation(ResultSet rs) throws SQLException {
        Location l = new Location();
        l.setLocationId(rs.getInt("location_id"));
        l.setCityName(rs.getString("city_name"));
        l.setStateName(rs.getString("state_name"));
        l.setCountryName(rs.getString("country_name"));
        l.setActive(rs.getBoolean("is_active"));
        return l;
    }
}
