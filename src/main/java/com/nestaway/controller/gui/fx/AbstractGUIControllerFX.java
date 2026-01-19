package com.nestaway.controller.gui.fx;

import com.nestaway.exception.NotFoundException;
import com.nestaway.exception.OperationFailedException;
import com.nestaway.utils.SessionManager;
import com.nestaway.utils.view.fx.FilesFXML;
import com.nestaway.utils.view.fx.PageManagerSingleton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
//classe astratta, definisce il comportamento base di ogni controller FX
public abstract class AbstractGUIControllerFX {

    protected Integer currentSession; //id sessione corrente

    @FXML
    Label errorMsg;

    @FXML
    Button home;

    @FXML
    Button login;

    //metodo per inizializzare i dati, chiamato dal PageManager dopo aver caricato il file FXML e creato il controller, serve a passare l'ID della sessione al controller
    public abstract void initialize(Integer session) throws OperationFailedException, NotFoundException;

    //metodo per mostrare i messaggi di errore
    public void setMsg(Label label, String msg){
        label.setText(msg);
        label.setVisible(true);
    }

    public void resetMsg(Label ... labels){
        for(Label label: labels){
            label.setVisible(false);
        }
    }

    public void goNext(String path){
        resetMsg(errorMsg); //pulisce eventuali errori precedenti
        try{
            //delego al page manager (singleton) il cambio scena, passando la sessione corrente
            PageManagerSingleton.getInstance().goNext(path, currentSession);
        }catch (OperationFailedException | NotFoundException e){
            setMsg(errorMsg,e.getMessage());
        }
    }

    @FXML
    public void goHome(){
        resetMsg(errorMsg);
        //soft reset, pulisco i parametri di ricerca ma mantengo il login
        SessionManager.getSessionManager().getSessionFromId(currentSession).softReset();
        try{
            //chiedo al pageManager di tornare alla schermata principale dell'utente
            PageManagerSingleton.getInstance().goHome(currentSession);
        }catch (OperationFailedException | NotFoundException e){
            setMsg(errorMsg,e.getMessage());
        }
    }

    @FXML
    public void logout(){
        resetMsg(errorMsg);
        //reset completo, utente loggato fuori
        SessionManager.getSessionManager().getSessionFromId(currentSession).reset();
        try{
            //chiedo al pageManager di tornare alla home pubblica
            PageManagerSingleton.getInstance().setHome(FilesFXML.HOME.getPath(), currentSession);
        }catch (OperationFailedException | NotFoundException e){
            setMsg(errorMsg,e.getMessage());
        }
    }

    //vado alla pagina di login
    @FXML
    public void login(){
        goNext(FilesFXML.LOGIN.getPath());
    }
}

