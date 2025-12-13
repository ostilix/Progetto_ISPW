package com.nestaway.dao.jdbc.queries;

import java.sql.*;
import java.time.LocalDate;

public final class ReviewQueries {

    private ReviewQueries() {
        throw new IllegalStateException("Utility class");
    }

    public static PreparedStatement insertReview(Connection conn, String bookingCode, int rating, String comment, LocalDate date, int idStay) throws SQLException {
        String query = "INSERT INTO Review (BookingCode, Rating, Comment, Date, idStay) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, bookingCode);
        pstmt.setInt(2, rating);
        pstmt.setString(3, comment);
        pstmt.setDate(4, java.sql.Date.valueOf(date));
        pstmt.setInt(5, idStay);
        return pstmt;
    }

    public static PreparedStatement selectByStay(Connection conn, Integer idStay) throws SQLException {
        String query = "SELECT *" + " FROM Review WHERE idStay = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setInt(1, idStay);
        return pstmt;
    }
}
