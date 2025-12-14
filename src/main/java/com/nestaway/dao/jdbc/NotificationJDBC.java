package com.nestaway.dao.jdbc;

import com.nestaway.dao.NotificationDAO;
import com.nestaway.dao.jdbc.queries.NotificationQueries;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Notification;
import com.nestaway.model.TypeNotif;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.nestaway.exception.dao.TypeDAOException.*;

public class NotificationJDBC implements NotificationDAO {

    private static final String COLUMN_TYPE = "Type";
    private static final String COLUMN_DATETIME = "DateTime";
    private static final String COLUMN_NAMESTAY = "NameStay";
    private static final String COLUMN_BOOKINGCODE = "BookingCode";

    @Override
    public List<Notification> selectNotifications(String idHost) throws DAOException {
        List<Notification> notifications = new ArrayList<>();
        try (PreparedStatement pstmt = NotificationQueries.selectNotifications(SingletonConnector.getConnection(), idHost)){
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(fromResultSet(rs));
                }
                rs.close();
            }
            return notifications;
        } catch (SQLException e) {
            throw new DAOException("Error selectNotifications: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public void addNotification(String idHost, Notification notification) throws DAOException {
        try {
            Timestamp timestamp = Timestamp.valueOf(notification.getDateAndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            try (PreparedStatement pstmt = NotificationQueries.addNotification(SingletonConnector.getConnection(), notification.getType().getId(), notification.getNameStay(), idHost, notification.getBookingCode(), timestamp)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DAOException("Notification already exists", DUPLICATE);
            }
            throw new DAOException("Error in addNotification: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public void deleteNotification(String idHost, List<Notification> notifications) throws DAOException {
        try {
            Connection conn = SingletonConnector.getConnection();
            for (Notification notif : notifications) {
                Timestamp timestamp = Timestamp.valueOf(notif.getDateAndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                try (PreparedStatement pstmt = NotificationQueries.deleteNotification(conn, idHost, notif.getNameStay(), notif.getBookingCode(), timestamp)) {
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error deleting notification(s): " + e.getMessage(), e, GENERIC);
        }
    }

    public void deleteNotificationByHost(String idHost) throws DAOException {
        try (PreparedStatement pstmt = NotificationQueries.deleteNotificationByHost(SingletonConnector.getConnection(), idHost)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error in deleteNotification: " + e.getMessage(), e, GENERIC);
        }
    }

    private Notification fromResultSet(ResultSet rs) throws SQLException {
        return new Notification(TypeNotif.valueOf(rs.getString(COLUMN_TYPE)), rs.getString(COLUMN_NAMESTAY), rs.getString(COLUMN_BOOKINGCODE), rs.getTimestamp(COLUMN_DATETIME).toLocalDateTime());
    }
}


