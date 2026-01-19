package com.nestaway.dao.demo;

import com.nestaway.dao.BookingDAO;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Booking;
import com.nestaway.utils.dao.DemoIndex;
import com.nestaway.utils.dao.MemoryDatabase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nestaway.exception.dao.TypeDAOException.*;
//implementazione dell'interfaccia DAO
public class BookingDEMO implements BookingDAO {

    @Override
    public Booking addBooking(Integer idStay, Booking booking) throws DAOException {
        try {
            //verifico se esiste già, scorro la mappa indice (codice -> idStay)
            boolean exists = DemoIndex.getBookingStayMap().entrySet().stream().anyMatch(entry -> {
                        String code = entry.getKey();
                        Integer mappedIdStay = entry.getValue();
                        return mappedIdStay.equals(idStay) && MemoryDatabase.getBookings().stream().filter(b -> b.getCodeBooking().equals(code)).anyMatch(b -> b.getEmailAddress().equalsIgnoreCase(booking.getEmailAddress()) && b.getTelephone().equals(booking.getTelephone()));
            });

            if (exists) {
                throw new DAOException("Booking already exists", DUPLICATE);
            }
            //genero id
            int idBooking = nextId(idStay);
            //imposto ID nel bean
            booking.setIdAndCodeBooking(idBooking);
            //aggiungo la prenotazione alla lista
            MemoryDatabase.getBookings().add(booking);
            //aggiorno indice
            DemoIndex.getBookingStayMap().put(booking.getCodeBooking(), idStay);

            return booking;
        } catch (Exception e) {
            throw new DAOException("Error in addBooking", e, GENERIC);
        }
    }

    @Override
    public List<Booking> selectBookingByStay(Integer idStay) throws DAOException {
        try {
            //trovo i codici delle prentazioni associati allo stay
            Set<String> codes = DemoIndex.getBookingStayMap().entrySet().stream().filter(entry -> entry.getValue().equals(idStay)).map(java.util.Map.Entry::getKey).collect(Collectors.toSet());
            //filtro e ritorno nella lista
            return MemoryDatabase.getBookings().stream().filter(b -> codes.contains(b.getCodeBooking())).toList();
        } catch (Exception e) {
            throw new DAOException("Error in selectBookingByStay", e, GENERIC);
        }
    }

    @Override
    public Booking selectBookingByCode(String codeBooking) throws DAOException {
        try {
            return MemoryDatabase.getBookings().stream().filter(b -> b.getCodeBooking().equals(codeBooking)).findFirst().orElse(null);
        } catch (Exception e) {
            throw new DAOException("Error in selectBookingByCode", e, GENERIC);
        }
    }

    //genero Id per le prenotazione di uno stay
    private int nextId(Integer idStay) {
        long count = DemoIndex.getBookingStayMap().values().stream().filter(stayId -> stayId.equals(idStay)).count();
        return (int) count + 1;
    }
}
