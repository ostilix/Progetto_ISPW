package com.nestaway.dao.jdbc.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;


public final class HostQueries {

    private HostQueries() {
        throw new IllegalStateException("Utility class");
    }

    public static PreparedStatement selectHost(Connection conn, String idHost) throws SQLException {
        String query = "SELECT *" + " FROM Host WHERE Username = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setString(1, idHost);
        return pstmt;
    }

    public static PreparedStatement selectHost(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT *" + " FROM Host WHERE Username = ? AND Password = ?";
        PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        return pstmt;
    }

    public static PreparedStatement insertHost(Connection conn, String username, String password, String firstName, String lastName, String email, String infoPayPal) throws SQLException {
        String query = "INSERT INTO Host (Username, Password, FirstName, LastName, EmailAddress, InfoPayPal) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        pstmt.setString(3, firstName);
        pstmt.setString(4, lastName);
        pstmt.setString(5, email);
        pstmt.setString(6, infoPayPal);
        return pstmt;
    }
}
