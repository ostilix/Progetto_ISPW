package com.nestaway.dao;

import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Booking;

import java.util.List;

public interface BookingDAO {
    Booking addBooking(Integer idStay, Booking booking) throws DAOException;
    List<Booking> selectBookingByStay(Integer idStay) throws DAOException;
    Booking selectBookingByCode(String codeBooking) throws DAOException;
}
