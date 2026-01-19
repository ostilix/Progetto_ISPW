package com.nestaway.utils;

import java.util.ArrayList;
import java.util.List;

//singleton, unico punto di accesso globale alla lista delle sessioni attive
public class SessionManager {
    //lista di tutte le istanze di Sessioni attive
    private final List<Session> activeSession = new ArrayList<>();
    //istanza statica unica
    private static SessionManager instance = null;
    //costruttore privato per impedire istanziazione diretta
    private SessionManager() {}

    //recupero l'istanza unica del sessionManager
    public static synchronized SessionManager getSessionManager(){
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    //creo una sessione vuota e la aggiungo alla lista
    public Integer createSession(){
        Session session = new Session();
        activeSession.add(session);
        return session.hashCode(); //funge da ID per la sessione
    }

    public Session getSessionFromId(Integer id){
        for(Session session: activeSession){
            if(session.hashCode() == id){
                return session;
            }
        }
        return null;
    }

    public void removeSession(Integer id) {
        activeSession.removeIf(session -> session.hashCode() == id);
    }
}


