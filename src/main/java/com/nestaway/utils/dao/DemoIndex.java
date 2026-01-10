package com.nestaway.utils.dao;

import java.util.HashMap;
import java.util.Map;

public class DemoIndex {
    //mappo codice prenotazione in id alloggio, mi serve nel dao del BookingDEMO
    private static final Map<String, Integer> bookingStayMap = new HashMap<>();

    private DemoIndex() {
    }

    public static Map<String, Integer> getBookingStayMap() {
        return bookingStayMap;
    }
}