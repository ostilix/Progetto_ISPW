package com.nestaway.dao.fs;

import com.nestaway.dao.StayDAO;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Availability;
import com.nestaway.model.Review;
import com.nestaway.model.Stay;
import com.nestaway.utils.dao.CSVHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.nestaway.exception.dao.TypeDAOException.*;
//implementazione interfaccia DAO
public class StayFS implements StayDAO {

    private static final String FILE_PATH = "src/main/resources/data/Stay.csv";
    private final CSVHandler csvHandler = new CSVHandler(FILE_PATH, ";");

    //trovo alloggio per idStay
    @Override
    public Stay selectStay(Integer idStay) throws DAOException {
        try {
            //leggo tutto il file
            List<String[]> rows = csvHandler.readAll();
            //itero per ogni riga
            for (String[] r : rows) {
                if (Integer.parseInt(r[0]) == idStay) {
                    //se trovo mathc id allora converto
                    Stay stay = fromCsvRecord(r);
                    stay.setTransientParams();
                    //carico i dati collegati
                    addReviewsAndAvailability(stay);
                    return stay;
                }
            }
            return null;
        } catch (IOException | NumberFormatException e) {
            throw new DAOException("Error in selectStay: " + e.getMessage(), e, GENERIC);
        }
    }

    //trovo alloggio per città
    @Override
    public List<Stay> selectStayByCity(String city) throws DAOException {
        try {
            List<Stay> stays = new ArrayList<>();
            for (String[] r : csvHandler.readAll()) {
                if (r[3].equalsIgnoreCase(city)) {
                    Stay stay = fromCsvRecord(r);
                    stay.setTransientParams();
                    addReviewsAndAvailability(stay);
                    stays.add(stay);
                }
            }
            return stays;
        } catch (IOException e) {
            throw new DAOException("Error in selectStayByCity: " + e.getMessage(), e, GENERIC);
        }
    }

    @Override
    public List<Stay> selectStayByHost(String hostUsername) throws DAOException {
        try {
            List<Stay> stays = new ArrayList<>();
            for (String[] r : csvHandler.readAll()) {
                if (r.length >= 10 && r[9].equals(hostUsername)) { // corretto
                    Stay stay = fromCsvRecord(r);
                    stay.setTransientParams();
                    addReviewsAndAvailability(stay);
                    stays.add(stay);
                }
            }
            return stays;
        } catch (IOException e) {
            throw new DAOException("Error in selectStayByHost: " + e.getMessage(), e, GENERIC);
        }
    }

    //inserisco nuovo alloggio
    public void insertStay(Stay stay) throws DAOException {
        try {
            List<String[]> allRows = csvHandler.readAll();
            //controllo duplicati
            for (String[] r : allRows) {
                if (r[0].equals(stay.getName()) && r[3].equals(stay.getCity())) {
                    throw new DAOException("Stay already exists", DUPLICATE);
                }
            }
            //calcolo id
            int newId = allRows.isEmpty() ? 1 : allRows.stream()
                    .mapToInt(r -> Integer.parseInt(r[0]))
                    .max()
                    .orElse(0) + 1;

            stay.setIdStay(newId);
            csvHandler.writeAll(Collections.singletonList(toCsvRecord(stay)));

        } catch (IOException | NumberFormatException e) {
            throw new DAOException("Error in insertStay: " + e.getMessage(), e, GENERIC);
        }
    }

    //ricerco per disponibilità
    @Override
    public List<Stay> selectAvailableStays(String city, LocalDate checkIn, LocalDate checkOut, int numGuests) throws DAOException {
        try {
            List<Stay> availableStays = new ArrayList<>();
            List<String[]> rows = csvHandler.readAll();

            for (String[] r : rows) {
                //primo filtro, citta e max guests
                if (r[3].equalsIgnoreCase(city) && Integer.parseInt(r[6]) >= numGuests) {
                    //creo oggetto stay temporaneo
                    Stay stay = fromCsvRecord(r);
                    stay.setTransientParams();
                    //carico tutte le disponibilità
                    addReviewsAndAvailability(stay);
                    //verifico disponibilità tramite model
                    if (stay.isAvailableInRange(checkIn, checkOut)) {
                        availableStays.add(stay);
                    }
                }
            }
            return availableStays;

        } catch (IOException | NumberFormatException e) {
            throw new DAOException("Error in selectAvailableStays: " + e.getMessage(), e, GENERIC);
        }
    }

    //da oggetto a CSV
    private String[] toCsvRecord(Stay stay) {
        return new String[]{
                stay.getName(),
                stay.getIdStay().toString(),
                stay.getDescription(),
                stay.getCity(),
                stay.getAddress(),
                stay.getPricePerNight().toString(),
                stay.getMaxGuests().toString(),
                stay.getNumRooms().toString(),
                stay.getNumBathrooms().toString(),
                stay.getHostUsername()
        };
    }

    //da CSV a oggetto
    private Stay fromCsvRecord(String[] r) {
        return new Stay(
                Integer.parseInt(r[0]),
                r[1],
                r[2],
                r[3],
                r[4],
                Double.parseDouble(r[5]),
                Integer.parseInt(r[6]),
                Integer.parseInt(r[7]),
                Integer.parseInt(r[8]),
                r[9]
        );
    }

    //caricamento dati correlati
    private void addReviewsAndAvailability(Stay stay) throws DAOException {
        if (stay == null) return;
        //istanzo i dao che mi servono
        ReviewFS reviewFS = new ReviewFS();
        AvailabilityFS availabilityFS = new AvailabilityFS();
        //recupero liste che mi servono
        List<Review> reviews = reviewFS.selectByStay(stay.getIdStay());
        List<Availability> availability = availabilityFS.selectByStay(stay.getIdStay());
        //aggiungo le liste allo stay
        stay.addReviews(reviews);
        stay.addAvailabilities(availability);
    }
}