package com.nestaway.controller.app;

import com.nestaway.bean.AvailabilityBean;
import com.nestaway.bean.BookingBean;
import com.nestaway.bean.StayBean;
import com.nestaway.dao.AvailabilityDAO;
import com.nestaway.dao.BookingDAO;
import com.nestaway.dao.HostDAO;
import com.nestaway.dao.StayDAO;
import com.nestaway.exception.DuplicateEntryException;
import com.nestaway.exception.IncorrectDataException;
import com.nestaway.exception.NotFoundException;
import com.nestaway.exception.OperationFailedException;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.*;
import com.nestaway.utils.ToBeanConverter;
import com.nestaway.utils.dao.factory.FactorySingletonDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.nestaway.exception.dao.TypeDAOException.DUPLICATE;
import static com.nestaway.exception.dao.TypeDAOException.NOT_AVAILABLE;

public class BookStayController {
    //riferimenti ai dao per recuperare e salvare i dati
    private final BookingDAO bookingDAO;
    private final StayDAO stayDAO;
    private final HostDAO hostDAO;
    private final AvailabilityDAO availabilityDAO;

    //inizializzo i DAO recuperandoli dalla factory, disaccoppio il controller dalla specifica implementazione scelta
    public BookStayController() {
        this.bookingDAO = FactorySingletonDAO.getDefaultDAO().getBookingDAO();
        this.stayDAO = FactorySingletonDAO.getDefaultDAO().getStayDAO();
        this.hostDAO = FactorySingletonDAO.getDefaultDAO().getHostDAO();
        this.availabilityDAO = FactorySingletonDAO.getDefaultDAO().getAvailabilityDAO();
    }

    //ritorno stayBean per passarli alla view
    public List<StayBean> findStays(String city, LocalDate from, LocalDate to, Integer numGuests) throws OperationFailedException, NotFoundException {
        try {
            //cerco alloggi disponibili tramite DAO
            List<Stay> stays = stayDAO.selectAvailableStays(city, from, to, numGuests);

            if (stays.isEmpty()) {
                throw new NotFoundException("No available stays found in " + city + " for the given period.");
            }

            //converto da Model a Bean per passarlo alla GUI
            List<StayBean> result = new ArrayList<>();
            for (Stay stay : stays) {
                result.add(ToBeanConverter.fromStayToStayBean(stay));
            }
            return result;

        } catch (DAOException e) {
            //loggo l'errore tecnico ma rilancio un'eccezione generica all'utente
            Logger.getGlobal().log(Level.WARNING, e.getMessage(), e.getCause());
            throw new OperationFailedException("DAO error in findStays");
        } catch (IncorrectDataException e) {
            //loggo l'errore tecnico ma rilancio un'eccezione generica all'utente
            Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e.getCause());
            throw new OperationFailedException("Invalid data in findStays");
        }
    }

    //recupero le disponibilità
    public List<AvailabilityBean> findAvailability(StayBean stayBean) throws OperationFailedException, NotFoundException {
        try {
            //verifico se l'alloggio esiste tramite DAO
            Stay stay = stayDAO.selectStay(stayBean.getIdStay());
            if (stay == null) {
                throw new NotFoundException("Stay not found.");
            }

            //recupero le disponibilità dal DAO
            List<Availability> availabilities = availabilityDAO.selectByStay(stayBean.getIdStay());
            if (availabilities.isEmpty()) {
                String msg = "No availability found for stay: " + stayBean.getIdStay();
                Logger.getGlobal().log(Level.SEVERE, msg);
                throw new OperationFailedException("No availability in findAvailability");
            }

            //converto da Model a Bean
            List<AvailabilityBean> availabilityBeans = new ArrayList<>();
            for (Availability a : availabilities) {
                availabilityBeans.add(ToBeanConverter.fromAvailabilityToAvailabilityBean(a));
            }
            return availabilityBeans;

        } catch (DAOException e) {
            //loggo l'errore tecnico ma rilancio un'eccezione generica all'utente
            Logger.getGlobal().log(Level.WARNING, e.getMessage(), e.getCause());
            throw new OperationFailedException("DAO error in findAvailability");
        } catch (IncorrectDataException e) {
            //loggo l'errore tecnico ma rilancio un'eccezione generica all'utente
            Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e.getCause());
            throw new OperationFailedException("Invalid data in findAvailability");
        }
    }

    //gestisco prenotazione
    public void sendReservation(StayBean stayBean, BookingBean bookingBean, List<AvailabilityBean> availabilityBean)
            throws OperationFailedException, DAOException, DuplicateEntryException {
        try {
            //verifico la validità delle date
            checkBookingValid(bookingBean, availabilityBean);

            //recupero dati host per pagamento e notifica
            Host host = hostDAO.selectHost(stayBean.getHostUsername());
            if (host == null) {
                String msg = "Inconsistent data. Host not found for stay: " + stayBean.getIdStay();
                Logger.getGlobal().log(Level.SEVERE, msg);
                throw new OperationFailedException("Host not found in sendReservation");
            }

            //creo il Model Booking dai dati del Bean
            Booking booking = new Booking(bookingBean.getFirstName(), bookingBean.getLastName(), bookingBean.getEmailAddress(),
                    bookingBean.getTelephone(), bookingBean.getCheckInDate(), bookingBean.getCheckOutDate(),
                    bookingBean.getNumGuests(), bookingBean.getOnlinePayment());

            //gestisco eventuale pagamento online
            if (bookingBean.getOnlinePayment().equals(true)) {
                Double pricePerNight = stayBean.getPricePerNight();
                long numNights = ChronoUnit.DAYS.between(bookingBean.getCheckInDate(), bookingBean.getCheckOutDate());
                Double amount = pricePerNight * numNights;
                //delego al controller del pagamento
                OnlinePaymentController onlinePaymentController = new OnlinePaymentController();
                //simulo pagamento
                boolean response = onlinePaymentController.payPayPal(host, amount, "Booking for stay: " + stayBean.getName());
                if (!response) {
                    throw new OperationFailedException("Payment failed in sendReservation");
                }
            }

            //salvo la prenotazione sul DB
            booking = bookingDAO.addBooking(stayBean.getIdStay(), booking);

            //invio notifica all'host
            NotificationsController notificationsController = new NotificationsController();
            notificationsController.notifyHost(new Notification(TypeNotif.NEW, stayBean.getName(), booking.getCodeBooking(), LocalDateTime.now()), host);

            //aggiorno il calendario occupando le date prenotate
            updateAvailabilityDates(stayBean, bookingBean.getCheckInDate(), bookingBean.getCheckOutDate());

            //aggiorno il Bean con il codeBooking per mostrarlo nella GUI
            bookingBean.setCodeBooking(booking.getCodeBooking());

        } catch (DAOException e) {
            if (e.getTypeException().equals(DUPLICATE)) {
                throw new DuplicateEntryException(e.getMessage() + ", if you have already paid, contact support.");
            } else if (e.getTypeException().equals(NOT_AVAILABLE)) {
                throw new OperationFailedException("No availabilities for this stay. If you have already paid, contact support.");
            } else {
                Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e.getCause());
                throw new OperationFailedException("General DAO error in sendReservation");
            }
        }
    }

    //helper per aggiornare date disponibilità
    public void updateAvailabilityDates(StayBean stayBean, LocalDate checkInDate, LocalDate checkOutDate)
            throws OperationFailedException {
        try {
            availabilityDAO.updateAvailability(checkInDate, checkOutDate, stayBean.getIdStay());
        } catch (DAOException e) {
            Logger.getGlobal().log(Level.WARNING, "DAO error in update availability", e);
            throw new OperationFailedException("DAO error in updateAvailabilityDates");
        }
    }

    //validazione interna
    private void checkBookingValid(BookingBean bookingBean, List<AvailabilityBean> availabilityBeans) throws OperationFailedException {
        LocalDate checkIn = bookingBean.getCheckInDate();
        LocalDate checkOut = bookingBean.getCheckOutDate();

        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new OperationFailedException("Invalid check-in/check-out dates.");
        }

        if (bookingBean.getNumGuests() == null || bookingBean.getNumGuests() <= 0) {
            throw new OperationFailedException("Invalid number of guests.");
        }

        //genero la lista delle date interessate dalla prenotazione
        List<LocalDate> requestedDates = checkIn.datesUntil(checkOut).toList();

        //per ogni giorno compreso nelle date verifico che sia disponibile
        for (LocalDate date : requestedDates) {
            boolean foundAvailable = availabilityBeans.stream().anyMatch(a -> date.equals(a.getDate()) && Boolean.TRUE.equals(a.getIsAvailable()));
            if (!foundAvailable) {
                throw new OperationFailedException("Date not available for booking: " + date);
            }
        }
    }
}
