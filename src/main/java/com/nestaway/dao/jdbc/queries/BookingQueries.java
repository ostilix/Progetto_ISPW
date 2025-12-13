package com.nestaway.dao.jdbc.queries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public final class BookingQueries {

    private BookingQueries() {
        throw new IllegalStateException("Utility class");
    }

    public static PreparedStatement insertBooking(Connection conn, String codeBooking, String firstName, String lastName, String emailAddress, String telephone, LocalDate checkInDate, LocalDate checkOutDate, int numGuests, int onlinePayment, Integer idStay) throws SQLException {

        String query = "INSERT INTO Booking (CodeBooking, FirstName, LastName, EmailAddress, Telephone, CheckInDate, CheckOutDate, NumGuests, OnlinePayment, Stay) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, codeBooking);
        pstmt.setString(2, firstName);
        pstmt.setString(3, lastName);
        pstmt.setString(4, emailAddress);
        pstmt.setString(5, telephone);
        pstmt.setDate(6, java.sql.Date.valueOf(checkInDate));
        pstmt.setDate(7, java.sql.Date.valueOf(checkOutDate));
        pstmt.setInt(8, numGuests);
        pstmt.setInt(9, onlinePayment);
        pstmt.setInt(10, idStay);
        return pstmt;
    }

    public static PreparedStatement countBookings(Connection conn, Integer idStay) throws SQLException {
        String query = "SELECT COUNT(*) FROM Booking WHERE Stay = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setInt(1, idStay);
        return pstmt;
    }

    public static PreparedStatement selectBookingByStay(Connection conn, Integer idStay) throws SQLException {
        String query = "SELECT *" + " FROM Booking WHERE Stay = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setInt(1, idStay);
        return pstmt;
    }
    public static PreparedStatement selectBookingByCode(Connection conn, String codeBooking) throws SQLException {
        String query = "SELECT *" + " FROM Booking WHERE CodeBooking = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setString(1, codeBooking);
        return pstmt;
    }
}
