package com.nestaway.controller.gui.cli;

import com.nestaway.bean.StayBean;
import com.nestaway.controller.app.BookStayController;
import com.nestaway.exception.NotFoundException;
import com.nestaway.exception.OperationFailedException;
import com.nestaway.utils.SessionManager;
import com.nestaway.utils.view.cli.ReturningHome;
import com.nestaway.view.cli.ListStaysView;

import java.time.LocalDate;
import java.util.List;

public class ListStaysGUIControllerCLI extends AbstractGUIControllerCLI{

    private final ListStaysView listStaysView = new ListStaysView();
    private List<StayBean> stays; //utilizzata come "cache" locale degli alloggi trovati
    //parametri di ricerca cached per non richiamare sessione ogni volta
    private final String city;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final Integer numGuests;

    //costruttore: recupero i parametri salvati nella sessione dal controller precedente
    ListStaysGUIControllerCLI(Integer session, ReturningHome returningHome){
        this.currentSession = session;
        this.city = SessionManager.getSessionManager().getSessionFromId(session).getCity();
        this.checkInDate = SessionManager.getSessionManager().getSessionFromId(session).getCheckIn();
        this.checkOutDate = SessionManager.getSessionManager().getSessionFromId(session).getCheckOut();
        this.numGuests = SessionManager.getSessionManager().getSessionFromId(session).getNumGuests();
        this.returningHome = returningHome;//oggetto passati tra i controller per gestire flusso di ritorno alla home
    }

    @Override
    public void start() {
        //istanzio il controller
        BookStayController bookStayController = new BookStayController();

        try {
            //trovo gli alloggi
            stays = bookStayController.findStays(city, checkInDate, checkOutDate, numGuests);

            int choice;
            choice = listStaysView.showMenu();

            switch (choice) {
                case 1 -> showStays();
                case 2 -> selectStay();
                case 3 -> goHome();
                case 4 -> exit();
                default -> throw new IllegalArgumentException("Invalid case!");
            }
        } catch (OperationFailedException e) {
            listStaysView.showError(e.getMessage());
        } catch (NotFoundException e) {
            listStaysView.showMessage(e.getMessage());
        }
    }

    //formatto e invio la lista degli alloggi alla view
    private void showStays() {
        //preparo array di stringhe
        String[] staysFormatted = new String[this.stays.size()];
        int i = 0;
        for (StayBean stay : this.stays) {
            staysFormatted[i] = String.format(
                    "%d - Name: %s%n" +
                            "    City: %s%n" +
                            "    Price: %.2f€/night%n" +
                            "    Rooms: %d%n",
                    i + 1,
                    stay.getName(),
                    stay.getCity(),
                    stay.getPricePerNight(),
                    stay.getNumRooms()
            );
            i++;
        }

        //passo l'array di stringhe alla View
        listStaysView.showStays(staysFormatted);
        //ricarico il menu
        start();
    }

    private void selectStay() {
        //recupero il numero dell'alloggio dalla view e lo verifico
        int num = listStaysView.selectStay();
        if (num < 1 || num > stays.size()) {
            listStaysView.showMessage("Stay not found!");
            start();
        } else {
            //recupero il Bean dell'alloggio corrispondente
            StayBean stay = stays.get(num - 1); //-1 perche la lista parte da 0
            if (stay != null) {
                //salvo l'alloggio scelto nella sessione per i prossimi controller
                SessionManager.getSessionManager().getSessionFromId(currentSession).setStay(stay);
                //passo al prossimo controller
                StayDetailsGUIControllerCLI stayDetailsController = new StayDetailsGUIControllerCLI(currentSession, returningHome);
                stayDetailsController.start();
            }
            //resetto la selezione per pulizia
            SessionManager.getSessionManager().getSessionFromId(currentSession).resetStay();
            //ricarico la pagina se non devo tornare alla home
            if (Boolean.FALSE.equals(returningHome.getReturningHome())) {
                start();
            }
        }
    }
}
