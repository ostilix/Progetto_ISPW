package com.nestaway.dao.fs;

import com.nestaway.dao.BookingDAO;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Booking;
import com.nestaway.utils.dao.CSVHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.nestaway.exception.dao.TypeDAOException.*;
//implementazione interfaccia DAO
public class BookingFS implements BookingDAO {

    private static final String FILE_PATH = "src/main/resources/data/Booking.csv";

    //aggiungo un nuovo booking
    @Override
    public Booking addBooking(Integer idStay, Booking booking) throws DAOException {
        try {
            CSVHandler handler = new CSVHandler(FILE_PATH, ";");
            //controllo se ci sono duplicati
            if (!handler.find(uniquePredicate(String.valueOf(idStay), booking.getEmailAddress(), booking.getTelephone())).isEmpty()) {
                throw new DAOException("Booking already exists", DUPLICATE);
            }

            //leggo tutte le stringhe per trovare ID più alto
            List<String[]> allRows = handler.readAll();
            int maxId = 0;
            for (String[] row : allRows) {
                int currentId = parseBookingId(row);
                if (currentId > maxId) {
                        maxId = currentId;
                }
            }

            int newId = maxId + 1;
            //aggiorno Booking
            booking.setIdAndCodeBooking(newId);

            //scrivo in coda
            List<String[]> rows = new ArrayList<>();
            rows.add(toCsvRecord(idStay, booking)); //converto in array CSV
            handler.writeAll(rows);
            return booking;
        } catch (IOException e) {
            throw new DAOException("Error in addBooking: " + e.getMessage(), e, GENERIC);
        }
    }

    //cerco Booking per alloggio
    @Override
    public List<Booking> selectBookingByStay(Integer idStay) throws DAOException {
        try {
            CSVHandler handler = new CSVHandler(FILE_PATH, ";");
            //filtro le righe dove colonna 9 corrisponde a ID
            List<String[]> found = handler.find(r -> r[9].equals(String.valueOf(idStay)));
            //converto array string in oggetto Booking e aggiungo alla lista
            return found.stream().map(this::fromCsvRecord).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new DAOException("Error in selectBookingByStay: " + e.getMessage(), e, GENERIC);
        }
    }

    //cerco Booking per codice prenotazione
    @Override
    public Booking selectBookingByCode(String codeBooking) throws DAOException {
        try {
            CSVHandler handler = new CSVHandler(FILE_PATH, ";");
            //cerco la riga dove la colonna 3 corrisponde al codice
            List<String[]> found = handler.find(r -> r[3].equals(codeBooking));
            if (found.isEmpty()) {
                return null;
            }
            //converto e ritorno l'oggetto trovato
            return fromCsvRecord(found.get(0));
        } catch (IOException e) {
            throw new DAOException("Error in selectBookingByCode: " + e.getMessage(), e, GENERIC);
        }
    }

    //converto BookingID in int
    private int parseBookingId(String[] row) throws DAOException {
        try {
            return Integer.parseInt(row[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new DAOException("Error in addBooking: " + e.getMessage(), e, GENERIC);
        }
    }

    //verifico se esiste un Booking con stessa mail o telefono
    private Predicate<String[]> uniquePredicate(String idStay, String email, String telephone) {
        return r -> (r[9].equals(idStay) && (r[4].equals(email) || r[5].equals(telephone)));
    }

    //converto da CSV a oggetto Booking
    private Booking fromCsvRecord(String[] r) {
        Booking booking = new Booking(
                r[0],
                r[1],
                r[4],
                r[5],
                LocalDate.parse(r[6]),
                LocalDate.parse(r[7]),
                Integer.parseInt(r[8]),
                Boolean.parseBoolean(r[10]));
        booking.setIdAndCodeBooking(r[3]);
        return booking;
    }

    //converto da oggetto Booking a CSV
    private String[] toCsvRecord(Integer idStay, Booking booking) {
        return new String[]{
                booking.getFirstName(),
                booking.getLastName(),
                String.valueOf(booking.getIdBooking()),
                booking.getCodeBooking(),
                booking.getEmailAddress(),
                booking.getTelephone(),
                booking.getCheckInDate().toString(),
                booking.getCheckOutDate().toString(),
                booking.getNumGuests().toString(),
                idStay.toString(),
                booking.getOnlinePayment().toString()};
    }
}