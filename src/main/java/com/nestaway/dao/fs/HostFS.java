package com.nestaway.dao.fs;

import com.nestaway.dao.HostDAO;
import com.nestaway.exception.EncryptionException;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Host;
import com.nestaway.model.Notification;
import com.nestaway.model.Stay;
import com.nestaway.utils.dao.CSVHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.nestaway.exception.dao.TypeDAOException.*;

//implementazione interfaccia DAO
public class HostFS implements HostDAO {

    private static final String FILE_PATH = "src/main/resources/data/Host.csv";
    private final CSVHandler csvHandler = new CSVHandler(FILE_PATH, ";");

    //seleziono host per id
    @Override
    public Host selectHost(String idHost) throws DAOException {
        try {
            //cerco riga dove la colonna 0 = idHost
            List<String[]> rows = csvHandler.find(r -> r[0].equals(idHost));
            if (rows.isEmpty()) return null;
            //prendo il risultato
            Host host = fromCsvRecord(rows.getFirst());
            //i parametri transitori servono al bean
            host.setTransientParams();
            //carico notifiche e alloggi collegati
            addNotifAndStays(host);
            return host;
        } catch (IOException e) {
            throw new DAOException("Error in selectHost by id: " + e.getMessage(), e, GENERIC);
        }
    }

    //seleziono per username e pass
    @Override
    public Host selectHost(String username, String password) throws DAOException {
        try {
            //cerco riga dove la colonna 0 = username e colonna 1 = pass
            List<String[]> rows = csvHandler.find(r -> r[0].equals(username) && r[1].equals(password));
            if (rows.isEmpty()) return null;
            //prendo il risultato
            Host host = fromCsvRecord(rows.getFirst());
            //i parametri transitori servono al bean
            host.setTransientParams();
            //carico notifiche e alloggi collegati
            addNotifAndStays(host);
            return host;
        } catch (IOException e) {
            throw new DAOException("Error in selectHost by credentials: " + e.getMessage(), e, GENERIC);
        }
    }

    //inserisco Host
    @Override
    public void insertHost(Host host) throws DAOException {
        try {
            //leggo tutto il file per controllare unicità
            List<String[]> allRows = csvHandler.readAll();
            boolean usernameExists = allRows.stream().anyMatch(r -> r[0].equals(host.getUsername()));
            boolean emailExists = allRows.stream().anyMatch(r -> r[2].equals(host.getEmailAddress()));
            if (usernameExists) {
                throw new DAOException("Username already exists", DUPLICATE);
            } else if (emailExists) {
                throw new DAOException("Host with same email already exists", DUPLICATE);
            }
            //scrivo la nuova riga
            csvHandler.writeAll(Collections.singletonList(toCsvRecord(host)));
        } catch (IOException e) {
            throw new DAOException("Error in insertHost: " + e.getMessage(), e, GENERIC);
        }
    }

    //da oggetto a CSV
    private String[] toCsvRecord(Host host) {
        return new String[]{
                host.getUsername(),
                host.getPassword(),
                host.getEmailAddress(),
                host.getFirstName(),
                host.getLastName(),
                host.getInfoPayPal()
        };
    }

    //da CSV a oggetto
    private Host fromCsvRecord(String[] r) throws DAOException {
        try {
            return new Host(
                    r[3],
                    r[4],
                    r[2],
                    r[0],
                    r[5],
                    r[1]);
        } catch (EncryptionException e) {
            throw new DAOException("Failed to create Host from CSV: " + e.getMessage(), e, GENERIC);
        }
    }

    private void addNotifAndStays(Host host) throws DAOException {
        if (host == null) return;
        //istanzo i dao che mi servono
        NotificationFS notificationFS = new NotificationFS();
        StayFS stayFS = new StayFS();
        // recupero le liste chiamando i DAO corrispondenti
        List<Stay> stays = stayFS.selectStayByHost(host.getUsername());
        List<Notification> notifications = notificationFS.selectNotifications(host.getUsername());
        //aggiungo le liste all'Host
        host.addStay(stays);
        host.addNotification(notifications);
    }
}