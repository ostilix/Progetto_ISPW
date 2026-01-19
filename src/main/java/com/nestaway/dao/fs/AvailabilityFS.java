package com.nestaway.dao.fs;

import com.nestaway.dao.AvailabilityDAO;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Availability;
import com.nestaway.utils.dao.CSVHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.nestaway.exception.dao.TypeDAOException.GENERIC;

//implemento interfaccia AvailabilityDAO
public class AvailabilityFS implements AvailabilityDAO {
    //percorso file csv
    private static final String FILE_PATH = "src/main/resources/data/Availability.csv";
    private final CSVHandler handler = new CSVHandler(FILE_PATH, ";");

    //recupero disponibilità per alloggio
    @Override
    public List<Availability> selectByStay(Integer idStay) throws DAOException {
        try {
            //controllo se elemento all'indice 3 corrisponde a quello cercato
            List<String[]> rows = handler.find(r -> Integer.parseInt(r[3]) == idStay);
            //inizializzo lista vuota
            List<Availability> result = new ArrayList<>();
            //itero sulle righe trovate
            for (String[] row : rows) {
                //converto da array di stringhe a oggetto e lo aggiungo
                result.add(fromCsvRecord(row));
            }
            return result;
        } catch (IOException e) {
            throw new DAOException("Error in selectByStay: " + e.getMessage(), e, GENERIC);
        }
    }

    //recupero disponibilità per range
    @Override
    public List<Availability> selectInRange(Integer idStay, LocalDate from, LocalDate to) throws DAOException {
        try {
            List<String[]> rows = handler.find(r -> {
                int stayId = Integer.parseInt(r[3]); //ID stay
                LocalDate date = LocalDate.parse(r[1]); //converto in formato LocalDate
                //verifico disponibilità
                return stayId == idStay && (!date.isBefore(from) && !date.isAfter(to));
            });
            //inzializzo lista vuota
            List<Availability> result = new ArrayList<>();
            for (String[] row : rows) {
                //converto e aggiungo alla lista
                result.add(fromCsvRecord(row));
            }
            return result;
        } catch (IOException e) {
            throw new DAOException("Error in selectInRange: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public void updateAvailability(LocalDate checkIn, LocalDate checkOut, Integer idStay) throws DAOException {
        try {
            //leggo tutto il file
            List<String[]> allRows = handler.readAll();
            boolean changed = false; //flag, se non trovo nulla non scrivo nulla

            //itero sulle righe
            for (String[] row : allRows) {
                int currentStayId = Integer.parseInt(row[3]);
                LocalDate date = LocalDate.parse(row[1]);
                //se la riga è dell'alloggio e nel range di date
                if (currentStayId == idStay && !date.isBefore(checkIn) && date.isBefore(checkOut)) {
                    row[2] = "false"; //false=occupato
                    changed = true; //flag, c'è stata una modifica
                }
            }
            //se ci sono cambiamenti
            if (changed) {
                //riscrivo tutto con i dati aggiornati
                handler.writeCleaned(allRows);
            }

        } catch (IOException e) {
            throw new DAOException("Error in updateAvailability: " + e.getMessage(), e, GENERIC);
        }
    }

    //cancello singola disponibilità
    @Override
    public void deleteAvailability(Integer idStay, LocalDate date) throws DAOException {
        try {
            //rimuovo se ID e Data corssipondono
            handler.remove(r -> Integer.parseInt(r[3]) == idStay && LocalDate.parse(r[1]).equals(date));
        } catch (IOException e) {
            throw new DAOException("Error in deleteAvailability: " + e.getMessage(), e, GENERIC);
        }
    }

    //cancello tutte le disponibilità
    @Override
    public void deleteAllByStay(Integer idStay) throws DAOException {
        try {
            //cancello per ID
            handler.remove(r -> Integer.parseInt(r[3]) == idStay);
        } catch (IOException e) {
            throw new DAOException("Error in deleteAllByStay: " + e.getMessage(), e, GENERIC);
        }
    }

    private Availability fromCsvRecord(String[] r) {
        return new Availability(
                Integer.parseInt(r[0]),
                LocalDate.parse(r[1]),
                Boolean.parseBoolean(r[2]),
                Integer.parseInt(r[3])
        );
    }

}