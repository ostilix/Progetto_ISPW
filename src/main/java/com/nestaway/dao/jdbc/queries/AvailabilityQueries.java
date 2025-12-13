package com.nestaway.dao.jdbc.queries;

import java.sql.*;
import java.time.LocalDate;

public final class AvailabilityQueries {

    private AvailabilityQueries() {
        throw new IllegalStateException("Utility class");
    }

    public static PreparedStatement selectByStay(Connection conn, Integer idStay) throws SQLException {
        String query = "SELECT *" + " FROM Availability WHERE IdStay = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setInt(1, idStay);
        return pstmt;
    }

    public static PreparedStatement selectInRange(Connection conn, Integer idStay, LocalDate from, LocalDate to) throws SQLException {
        String query = "SELECT *" + " FROM Availability WHERE IdStay = ? AND Date BETWEEN ? AND ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setInt(1, idStay);
        pstmt.setDate(2, java.sql.Date.valueOf(from));
        pstmt.setDate(3, java.sql.Date.valueOf(to));
        return pstmt;
    }

    public static PreparedStatement updateAvailability(Connection conn, LocalDate checkIn, LocalDate checkOut, Integer idStay) throws SQLException {
        String query = "UPDATE Availability SET IsAvailable = 0 WHERE IdStay = ? AND Date >= ? AND Date < ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, idStay);
        pstmt.setDate(2, java.sql.Date.valueOf(checkIn));
        pstmt.setDate(3, java.sql.Date.valueOf(checkOut));
        return pstmt;
    }

    public static PreparedStatement deleteAvailability(Connection conn, Integer idStay, LocalDate date) throws SQLException {
        String query = "DELETE FROM Availability WHERE IdStay = ? AND Date = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, idStay);
        pstmt.setDate(2, java.sql.Date.valueOf(date));
        return pstmt;
    }

    public static PreparedStatement deleteAllByStay(Connection conn, Integer idStay) throws SQLException {
        String query = "DELETE FROM Availability WHERE IdStay = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, idStay);
        return pstmt;
    }
}
