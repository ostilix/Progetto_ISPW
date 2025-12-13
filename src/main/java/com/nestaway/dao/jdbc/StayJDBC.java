package com.nestaway.dao.jdbc;

import com.nestaway.dao.StayDAO;
import com.nestaway.dao.jdbc.queries.StayQueries;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Stay;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.nestaway.exception.dao.TypeDAOException.GENERIC;

public class StayJDBC implements StayDAO {

    private static final String COLUMN_ID = "IdStay";
    private static final String COLUMN_NAME = "NameStay";
    private static final String COLUMN_DESCRIPTION = "Description";
    private static final String COLUMN_CITY = "City";
    private static final String COLUMN_ADDRESS = "Address";
    private static final String COLUMN_PRICE = "PricePerNight";
    private static final String COLUMN_MAXGUESTS = "MaxGuests";
    private static final String COLUMN_NUMROOMS = "NumRooms";
    private static final String COLUMN_NUMBATHROOMS = "NumBathrooms";
    private static final String COLUMN_HOSTUSERNAME = "HostUsername";

    @Override
    public List<Stay> selectStayByCity(String city) throws DAOException {
        List<Stay> stays = new ArrayList<>();
        try (PreparedStatement pstmt = StayQueries.selectStayByCity(SingletonConnector.getConnection(), city)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stays.add(fromResultSet(rs));
            }
            return stays;
        } catch (SQLException e) {
            throw new DAOException("Error in selectStayByCity: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public List<Stay> selectStayByHost(String idHost) throws DAOException {
        List<Stay> stays = new ArrayList<>();
        try (PreparedStatement pstmt = StayQueries.selectStayByHost(SingletonConnector.getConnection(), idHost)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stays.add(fromResultSet(rs));
            }
            return stays;
        } catch (SQLException e) {
            throw new DAOException("Error in selectStayByHost: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public Stay selectStay(Integer idStay) throws DAOException {
        try (PreparedStatement pstmt = StayQueries.selectStay(SingletonConnector.getConnection(), idStay)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return fromResultSet(rs);
            }
            throw new DAOException("Stay with ID " + idStay + " not found", GENERIC);
        } catch (SQLException e) {
            throw new DAOException("Error in selectStay: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public List<Stay> selectAvailableStays(String city, LocalDate checkIn, LocalDate checkOut, int numGuests) throws DAOException {
        List<Stay> availableStays = new ArrayList<>();

        long numNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (numNights <= 0) return availableStays;

        try (PreparedStatement pstmt = StayQueries.selectAvailableStays(SingletonConnector.getConnection(), city, numGuests, checkIn, checkOut, numNights)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                availableStays.add(fromResultSet(rs));
            }
            return availableStays;

        } catch (SQLException e) {
            throw new DAOException("Error in selectAvailableStays: " + e.getMessage(), e, GENERIC);
        }
    }

    private Stay fromResultSet(ResultSet rs) throws SQLException {
        return new Stay(rs.getInt(COLUMN_ID), rs.getString(COLUMN_NAME), rs.getString(COLUMN_DESCRIPTION), rs.getString(COLUMN_CITY), rs.getString(COLUMN_ADDRESS), rs.getDouble(COLUMN_PRICE), rs.getInt(COLUMN_MAXGUESTS), rs.getInt(COLUMN_NUMROOMS), rs.getInt(COLUMN_NUMBATHROOMS), rs.getString(COLUMN_HOSTUSERNAME));
    }
}


