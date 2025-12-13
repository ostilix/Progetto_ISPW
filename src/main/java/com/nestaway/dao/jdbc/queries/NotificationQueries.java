package com.nestaway.dao.jdbc.queries;

import java.sql.*;

public final class NotificationQueries {

    private NotificationQueries() {
        throw new IllegalStateException("Utility class");
    }

    public static PreparedStatement selectNotifications(Connection conn, String idHost) throws SQLException {
        String query = "SELECT *" + " FROM Notification WHERE Host = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setString(1, idHost);
        return pstmt;
    }

    public static PreparedStatement addNotification(Connection conn, Integer type, String nameStay, String idHost, String bookingCode, Timestamp timestamp) throws SQLException {
        String query = "INSERT INTO Notification (Type, NameStay, Host, BookingCode, DateTime) " + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, type);
        pstmt.setString(2, nameStay);
        pstmt.setString(3, idHost);
        pstmt.setString(4, bookingCode);
        pstmt.setTimestamp(5, timestamp);
        return pstmt;
    }

    public static PreparedStatement deleteNotification(Connection conn, String idHost, String nameStay, String bookingCode, Timestamp timestamp) throws SQLException {
        String query = "DELETE FROM Notification WHERE Host = ? AND NameStay = ? AND BookingCode = ? AND DateTime = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, idHost);
        pstmt.setString(2, nameStay);
        pstmt.setString(3, bookingCode);
        pstmt.setTimestamp(4, timestamp);
        return pstmt;
    }

    public static PreparedStatement deleteNotificationByHost(Connection conn, String idHost) throws SQLException {
        String query = "DELETE FROM Notification WHERE Host = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, idHost);
        return pstmt;
    }
}

