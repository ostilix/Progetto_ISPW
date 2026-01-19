package com.nestaway.dao.demo;

import com.nestaway.dao.StayDAO;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Availability;
import com.nestaway.model.Stay;
import com.nestaway.utils.dao.MemoryDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.nestaway.exception.dao.TypeDAOException.GENERIC;
//implementazione dell'interfaccia DAO
public class StayDEMO implements StayDAO {

    @Override
    public Stay selectStay(Integer idStay) throws DAOException {
        try {
            for (Stay stay : MemoryDatabase.getStays()) {
                if (stay.getIdStay().equals(idStay)) {
                    //quando carico uno stay, ricarico le disponibilità attuali
                    refreshAvailability(stay);
                    return stay;
                }
            }
            return null; //non trovato
        } catch (Exception e) {
            throw new DAOException("Error in selectStay: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public List<Stay> selectStayByCity(String city) throws DAOException {
        try {
            List<Stay> result = new ArrayList<>();
            for (Stay stay : MemoryDatabase.getStays()) {
                if (stay.getCity().equalsIgnoreCase(city)) {
                    refreshAvailability(stay);
                    result.add(stay);
                }
            }
            return result;
        } catch (Exception e) {
            throw new DAOException("Error in selectStayByCity: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public List<Stay> selectStayByHost(String hostUsername) throws DAOException {
        try {
            List<Stay> result = new ArrayList<>();
            for (Stay stay : MemoryDatabase.getStays()) {
                if (stay.getHostUsername().equals(hostUsername)) {
                    refreshAvailability(stay);
                    result.add(stay);
                }
            }
            return result;
        } catch (Exception e) {
            throw new DAOException("Error in selectStayByHost: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public List<Stay> selectAvailableStays(String city, LocalDate checkIn, LocalDate checkOut, int numGuests) throws DAOException {
        try {
            List<Stay> allStays = MemoryDatabase.getStays();
            List<Stay> result = new ArrayList<>();

            for (Stay stay : allStays) {
                //filtro per citta e capienza
                if (stay.getCity().equalsIgnoreCase(city) && stay.getMaxGuests() >= numGuests) {
                    //carico disponibilità
                    refreshAvailability(stay);
                    //delego al model
                    if (stay.isAvailableInRange(checkIn, checkOut)) {
                        result.add(stay);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            throw new DAOException("Error in selectAvailableStays: " + e.getMessage(), e, GENERIC);
        }
    }

    private void refreshAvailability(Stay stay) {
        List<Availability> stayAvailabilities = new ArrayList<>();
        //cerco availability dello stay
        for (Availability a : MemoryDatabase.getAvailabilities()) {
            if (a.getIdStay().equals(stay.getIdStay())) {
                stayAvailabilities.add(a);
            }
        }
        //imposto la lista aggiornata nello stay
        stay.setAvailability(stayAvailabilities);
    }
}
