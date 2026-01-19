package com.nestaway.controller.gui.fx;

import com.nestaway.exception.NotFoundException;
import com.nestaway.exception.OperationFailedException;
import com.nestaway.utils.Session;
import com.nestaway.utils.SessionManager;
import com.nestaway.utils.view.fx.FilesFXML;
import com.nestaway.utils.view.fx.PageManagerSingleton;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;

import java.time.LocalDate;

public class HomeGUIControllerFX extends AbstractGUIControllerFX{
    @FXML
    Label message;

    @FXML
    Button searchButton;

    @FXML
    TextField searchCity;

    @FXML
    TextField searchCheckIn;

    @FXML
    TextField searchCheckOut;

    @FXML
    TextField searchGuests;

    @FXML
    ImageView backgroundImage;

    @FXML
    AnchorPane rootPane;


    @FXML
    public void searchCityEnter(KeyEvent event){
        if(event.getCode().toString().equals("ENTER")) {
            searchStay(); //avvio ricerca come se avessi cliccato il bottone cerca
        }
    }

    @FXML
    public void searchStay() {
        resetMsg(errorMsg, message); //pulisco messaggi vecchi

        //recupero i dati dai campi
        String city = searchCity.getText();
        String checkInStr = searchCheckIn.getText();
        String checkOutStr = searchCheckOut.getText();
        String guestsStr = searchGuests.getText();

        //valido i campi
        if (city.isEmpty() || checkInStr.isEmpty() || checkOutStr.isEmpty() || guestsStr.isEmpty()) {
            setMsg(message, "Insert all fields to continue!");
            return;
        }

        LocalDate checkIn;
        LocalDate checkOut;
        int guests;
        //converto e valido le date
        try {
            checkIn = LocalDate.parse(checkInStr);
            checkOut = LocalDate.parse(checkOutStr);
        } catch (Exception _) {
            setMsg(message, "Dates must be in the format yyyy-MM-dd.");
            return;
        }

        if (checkIn.isBefore(LocalDate.now())) {
            setMsg(message, "Check-in date must be today or later.");
            return;
        }

        if (!checkOut.isAfter(checkIn)) {
            setMsg(message, "Check-out must be after check-in.");
            return;
        }
        //converto e valido guests
        try {
            guests = Integer.parseInt(guestsStr);
            if (guests <= 0) {
                setMsg(message, "Number of guests must be greater than zero.");
                return;
            }
        } catch (NumberFormatException _) {
            setMsg(message, "Guests must be a valid positive number.");
            return;
        }
        //se tutto è valido salvo i parametri nella sessione
        try {
            SessionManager.getSessionManager().getSessionFromId(currentSession).setCity(city);
            SessionManager.getSessionManager().getSessionFromId(currentSession).setCheckIn(checkIn);
            SessionManager.getSessionManager().getSessionFromId(currentSession).setCheckOut(checkOut);
            SessionManager.getSessionManager().getSessionFromId(currentSession).setNumGuests(guests);
            //mostro i risultati della ricerca
            PageManagerSingleton.getInstance().goNext(FilesFXML.LIST_STAYS.getPath(), currentSession);
        } catch (OperationFailedException e) {
            setMsg(errorMsg, e.getMessage());
        } catch (NotFoundException e) {
            setMsg(message, e.getMessage());
        }
    }

    //inizializzo controller
    @Override
    public void initialize(Integer session) {
        this.currentSession = session;
        resetMsg(errorMsg, message);

        backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());

        //recupero sessione
        Session userSession = SessionManager.getSessionManager().getSessionFromId(currentSession);
        //se l'utente ha già cercato qualcosa recupero quei dati e li rimetto automaticamente nei campi
        if (userSession.getCity() != null) {
            searchCity.setText(userSession.getCity());
        }

        if (userSession.getCheckIn() != null) {
            searchCheckIn.setText(userSession.getCheckIn().toString());
        }

        if (userSession.getCheckOut() != null) {
            searchCheckOut.setText(userSession.getCheckOut().toString());
        }

        if (userSession.getNumGuests() != null) {
            searchGuests.setText(String.valueOf(userSession.getNumGuests()));
        }
    }
}
