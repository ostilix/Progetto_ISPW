package com.nestaway.controller.gui.cli;

import com.nestaway.utils.Session;
import com.nestaway.utils.SessionManager;
import com.nestaway.utils.view.cli.ReturningHome;
import com.nestaway.view.cli.HomeView;

import java.time.LocalDate;

public class HomeGUIControllerCLI extends AbstractGUIControllerCLI {
    //istanza della View per gestire I/O
    private final HomeView view = new HomeView();

    public HomeGUIControllerCLI(Integer session, ReturningHome returningHome) {
        this.currentSession = session; //serve per recuperare dati utente da SessionManager
        this.returningHome = returningHome; //oggetto passati tra i controller per gestire flusso di ritorno alla home
    }

    public void start() {
        int choice;
        choice = view.showMenu();

        switch (choice) {
            case 1 -> searchStays();
            case 2 -> loginPage();
            case 3 -> exit();
            default -> throw new IllegalArgumentException("Invalid case!");
        }
    }

    private void loginPage() {
        //creo il controller successivo passando sessione e gestore navigazione
        LoginAndRegisterGUIControllerCLI loginAndRegisterGUIController = new LoginAndRegisterGUIControllerCLI(currentSession, returningHome);
        //avvio il controller nuovo
        loginAndRegisterGUIController.start();
        //resetto il flag quando start() ritorna (utente loggato fuori)
        returningHome.setReturningHome(false);
        //ricarico la home
        start();
    }

    private void searchStays() {
        //chiedo alla View i dati per la ricerca
        String[] data = view.searchStay();

        //estraggo i dati ritornati dalla view
        String city = data[0].trim();
        String checkInStr = data[1].trim();
        String checkOutStr = data[2].trim();
        String guestsStr = data[3].trim();

        //valido i dati
        if (city.isEmpty() || checkInStr.isEmpty() || checkOutStr.isEmpty() || guestsStr.isEmpty()) {
            System.out.println("All fields are required. Please try again.");
            start(); //ricarico il menu in caso di errore
            return;
        }

        LocalDate checkIn;
        LocalDate checkOut;
        int guests;

        //conversione date
        try {
            checkIn = LocalDate.parse(checkInStr);
            checkOut = LocalDate.parse(checkOutStr);
        } catch (Exception _) {
            System.out.println("Dates must be in the format yyyy-MM-dd.");
            start();
            return;
        }

        LocalDate today = LocalDate.now();

        //controllo checkIn
        if (checkIn.isBefore(today)) {
            System.out.println("Check-in date cannot be in the past.");
            start();
            return;
        }

        //controllo checkOut
        if (!checkOut.isAfter(checkIn)) {
            System.out.println("Check-out date must be after check-in date.");
            start();
            return;
        }

        try {
            guests = Integer.parseInt(guestsStr);
            if (guests <= 0) {
                System.out.println("Number of guests must be greater than zero.");
                start();
                return;
            }
        } catch (NumberFormatException _) {
            System.out.println("Guests must be a valid positive number.");
            start();
            return;
        }

        //salvo i dati per la ricerca nella sessione
        //permette al controller successivo di accedere ai dati senza passarli nel costruttore
        Session session = SessionManager.getSessionManager().getSessionFromId(currentSession);
        session.setCity(city);
        session.setCheckIn(checkIn);
        session.setCheckOut(checkOut);
        session.setNumGuests(guests);

        //passo i dati al controller
        ListStaysGUIControllerCLI listStaysController = new ListStaysGUIControllerCLI(currentSession, returningHome);
        listStaysController.start();

        //soft reset(pulisco la sessione)
        session.resetCity();
        session.resetCheckIn();
        session.resetCheckOut();
        session.resetNumGuests();

        returningHome.setReturningHome(false);
        start();
    }

}

