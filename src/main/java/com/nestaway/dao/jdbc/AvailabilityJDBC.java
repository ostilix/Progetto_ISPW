package com.nestaway.dao.jdbc;

import com.nestaway.dao.AvailabilityDAO;
import com.nestaway.dao.jdbc.queries.AvailabilityQueries;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Availability;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.nestaway.exception.dao.TypeDAOException.*;

//implementazione dell'interfaccia DAO
public class AvailabilityJDBC implements AvailabilityDAO {
    //mappo i nomi delle colonne del db
    private static final String COLUMN_ID = "IdAvailability";
    private static final String COLUMN_DATE = "Date";
    private static final String COLUMN_IS_AVAILABLE = "IsAvailable";
    private static final String COLUMN_ID_STAY = "IdStay";

    @Override
    public List<Availability> selectByStay(Integer idStay) throws DAOException {
        //inizializzo lista vuota
        List<Availability> availabilities = new ArrayList<>();
        //preparo la query
        try (PreparedStatement pstmt = AvailabilityQueries.selectByStay(SingletonConnector.getConnection(), idStay)){
            //eseguo la query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    //converto riga DB in oggetto Availability e lo aggiungo alla lista
                    availabilities.add(fromResultSet(rs));
                }
            }
            return availabilities;
        } catch (SQLException e) {
            throw new DAOException("Error in selectByStay: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public List<Availability> selectInRange(Integer idStay, LocalDate from, LocalDate to) throws DAOException {
        //inizializzo lista vuota
        List<Availability> availabilities = new ArrayList<>();
        //preparo la query
        try (PreparedStatement pstmt = AvailabilityQueries.selectInRange(SingletonConnector.getConnection(), idStay, from, to)){
            //eseguo la query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    //converto riga DB in oggetto Availability e lo aggiungo alla lista
                    availabilities.add(fromResultSet(rs));
                }
            }
            return availabilities;
        } catch (SQLException e) {
            throw new DAOException("Error in selectInRange: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public void updateAvailability(LocalDate checkIn, LocalDate checkOut, Integer idStay) throws DAOException {
        //preparo la query
        try (PreparedStatement pstmt = AvailabilityQueries.updateAvailability(SingletonConnector.getConnection(), checkIn, checkOut, idStay)) {
            //restituisce numero di righe modificate
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error in updateAvailability " + e.getMessage(), e, GENERIC);
        }
    }


    @Override
    public void deleteAvailability(Integer idStay, LocalDate date) throws DAOException {
        //preparo la query
        try (PreparedStatement pstmt = AvailabilityQueries.deleteAvailability(SingletonConnector.getConnection(), idStay, date)) {
            //restituisce numero di righe modificate
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error in deleteAvailability: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public void deleteAllByStay(Integer idStay) throws DAOException {
        //preparo la query
        try (PreparedStatement pstmt = AvailabilityQueries.deleteAllByStay(SingletonConnector.getConnection(), idStay)) {
            //restituisce numero di righe modificate
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error in deleteAllByStay: " + e.getMessage(), e, GENERIC);
        }
    }

    private Availability fromResultSet(ResultSet rs) throws SQLException {
        return new Availability(
                rs.getInt(COLUMN_ID),
                rs.getDate(COLUMN_DATE).toLocalDate(),
                rs.getBoolean(COLUMN_IS_AVAILABLE),
                rs.getInt(COLUMN_ID_STAY)
        );
    }
}

