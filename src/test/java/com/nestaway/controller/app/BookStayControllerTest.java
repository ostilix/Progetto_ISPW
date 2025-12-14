package com.nestaway.controller.app;

import com.nestaway.bean.AvailabilityBean;
import com.nestaway.bean.BookingBean;
import com.nestaway.bean.StayBean;
import com.nestaway.utils.dao.factory.FactorySingletonDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BookStayControllerTest {

    private static BookStayController controller;

    @BeforeAll
    static void setup() throws Exception{
        Field instance = FactorySingletonDAO.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        System.setProperty("DAO_TYPE", "JDBC");
        controller = new BookStayController();
    }

    @Test
    void testFindStays() {
        assertDoesNotThrow(() -> {
            List<StayBean> stays = controller.findStays("Rome", LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 11), 2);
            assertFalse(stays.isEmpty(), "Should return some stays for Rome");
        });
    }

    @Test
    void testFindAvailability() throws Exception {
        List<StayBean> stays = controller.findStays("Milan", LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 11), 2);
        StayBean stay = stays.get(0);
        List<AvailabilityBean> av = controller.findAvailability(stay);
        assertNotNull(av);
        assertFalse(av.isEmpty(), "Availability should not be empty");
    }

    @Test
    void testSendReservation() throws Exception {
        List<StayBean> stays = controller.findStays("Rome", LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 11), 2);
        StayBean stay = stays.get(0);
        List<AvailabilityBean> av = controller.findAvailability(stay);

        BookingBean booking = new BookingBean();
        booking.setFirstName("Test");
        booking.setLastName("User");
        int randomId = new Random().nextInt(10000);

        booking.setEmailAddress("test" + randomId + "@example.com");
        booking.setTelephone("+391234567890");
        booking.setCheckInDate(LocalDate.of(2026, 9, 10));
        booking.setCheckOutDate(LocalDate.of(2026, 9, 11));
        booking.setNumGuests(2);
        booking.setOnlinePayment(false);

        controller.sendReservation(stay, booking, av);
        assertNotNull(booking.getCodeBooking(), "Booking code must be set after reservation");
    }
}
