package com.nestaway.utils;

import com.nestaway.bean.StayBean;
import com.nestaway.bean.UserBean;

import java.time.LocalDate;
//rappresenta una singola sessione utente
public class Session {

    private UserBean user; //utente loggato
    private StayBean stay; //alloggio selezionato
    //parametri di ricerca
    private String city;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer numGuests;

    public void setUser(UserBean user) {
        this.user = user;
    }

    public UserBean getUser() {
        return user;
    }

    public void setStay(StayBean stay) {
        this.stay = stay;
    }

    public StayBean getStay() {
        return stay;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setNumGuests(Integer numGuests) {
        this.numGuests = numGuests;
    }

    public Integer getNumGuests() {
        return numGuests;
    }

    //reset completo della sessione
    public void reset(){
        this.user = null;
        this.stay = null;
        this.city = null;
        this.checkIn = null;
        this.checkOut = null;
        this.numGuests = null;
    }

    //mantengo utente loggato
    public void softReset(){
        this.stay = null;
        this.city = null;
        this.checkIn = null;
        this.checkOut = null;
        this.numGuests = null;
    }

    public void resetStay(){
        this.stay = null;
    }

    public void resetCity(){
        this.city = null;
    }

    public void resetCheckIn(){
        this.checkIn = null;
    }

    public void resetCheckOut(){
        this.checkOut = null;
    }

    public void resetNumGuests(){
        this.numGuests = null;
    }
}
