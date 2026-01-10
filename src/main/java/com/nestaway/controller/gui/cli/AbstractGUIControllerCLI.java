package com.nestaway.controller.gui.cli;

import com.nestaway.utils.SessionManager;
import com.nestaway.utils.view.cli.ReturningHome;
//definisco la struttura base di ogni controller CLI
public abstract class AbstractGUIControllerCLI {

    protected Integer currentSession; //serve per recuperare dati utente da SessionManager
    protected ReturningHome returningHome;

    //avvio il menu
    public abstract void start();

    //chiudo l'applicazione
    protected void exit(){
        //rimuovo la sessione
        SessionManager.getSessionManager().removeSession(currentSession);
        System.out.println("Exiting...Bye bye!");
        System.exit(0);
    }

    protected void goBack() {
        returningHome.setReturningHome(false);
    }

    protected void goHome() {
        //resetto parametri di ricerca della sessione ma mantengo il login
        SessionManager.getSessionManager().getSessionFromId(currentSession).softReset();
        returningHome.setReturningHome(true);
    }

    protected void logout() {
        //rimuovo anche l'utente loggato
        SessionManager.getSessionManager().getSessionFromId(currentSession).reset();
        returningHome.setReturningHome(true);
    }
}

