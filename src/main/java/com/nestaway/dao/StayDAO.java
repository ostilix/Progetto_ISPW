package com.nestaway.dao;

import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Stay;

import java.time.LocalDate;
import java.util.List;

public interface StayDAO {
    Stay selectStay(Integer idStay) throws DAOException;
    List<Stay> selectStayByCity(String city) throws DAOException;
    List<Stay> selectStayByHost(String hostUsername) throws DAOException;
    List<Stay> selectAvailableStays(String city, LocalDate checkIn, LocalDate checkOut, int numGuests) throws DAOException;
}
