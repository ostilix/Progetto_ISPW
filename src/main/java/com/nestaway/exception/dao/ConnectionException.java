package com.nestaway.exception.dao;
//imposta automaticamente il tipo di errore a GENERIC, il fallimento di connessione è solitamente
// non recuperabile dall'utente
public class ConnectionException extends DAOException {

    public ConnectionException() {
        super(TypeDAOException.GENERIC);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause, TypeDAOException.GENERIC);
    }
}
