package com.nestaway.dao;

import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Availability;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityDAO {
    List<Availability> selectByStay(Integer idStay) throws DAOException;
    List<Availability> selectInRange(Integer idStay, LocalDate from, LocalDate to) throws DAOException;
    void updateAvailability(LocalDate checkIn, LocalDate checkOut, Integer idStay) throws DAOException;
    void deleteAvailability(Integer idStay, LocalDate date) throws DAOException;
    void deleteAllByStay(Integer idStay) throws DAOException;
}
