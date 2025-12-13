package com.nestaway.dao.jdbc.queries;

import java.sql.*;
import java.time.LocalDate;

public final class StayQueries {

    private StayQueries() {
        throw new IllegalStateException("Utility class");
    }

    public static PreparedStatement selectStay(Connection conn, Integer idStay) throws SQLException {
        String query = "SELECT *" + " FROM Stay WHERE IdStay = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setInt(1, idStay);
        return pstmt;
    }

    public static PreparedStatement selectStayByCity(Connection conn, String city) throws SQLException {
        String query = "SELECT *" + " FROM Stay WHERE City = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setString(1, city);
        return pstmt;
    }

    public static PreparedStatement selectStayByHost(Connection conn, String idHost) throws SQLException {
        String query = "SELECT *" + "FROM Stay WHERE HostUsername = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setString(1, idHost);
        return pstmt;
    }

    public static PreparedStatement selectAvailableStays(Connection conn, String city, int numGuests, LocalDate checkIn, LocalDate checkOut, long numNights) throws SQLException {
        String query = "SELECT s.* " +
                "FROM Stay s " +
                "JOIN Availability a ON s.IdStay = a.IdStay " +
                "WHERE s.City = ? " +
                "AND s.MaxGuests >= ? " +
                "AND a.Date >= ? AND a.Date < ? " +
                "AND a.IsAvailable = 1 " +
                "GROUP BY s.IdStay, s.NameStay, s.Description, s.City, s.Address, s.PricePerNight, s.MaxGuests, s.NumRooms, s.NumBathrooms, s.HostUsername " +
                "HAVING COUNT(DISTINCT a.Date) = ?"; // 5

        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        pstmt.setString(1, city);
        pstmt.setInt(2, numGuests);
        pstmt.setString(3, checkIn.toString());
        pstmt.setString(4, checkOut.toString());
        pstmt.setLong(5, numNights);

        return pstmt;
    }
}
