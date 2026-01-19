package com.nestaway.dao.demo;

import com.nestaway.dao.HostDAO;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Host;
import com.nestaway.model.Notification;
import com.nestaway.model.Stay;
import com.nestaway.utils.dao.MemoryDatabase;

import java.util.List;

import static com.nestaway.exception.dao.TypeDAOException.*;
//implementazione dell'interfaccia DAO
public class HostDEMO implements HostDAO {

    @Override
    public Host selectHost(String idHost) throws DAOException {
        try {
            //cerco host filtrando per username
            Host host = MemoryDatabase.getHosts().stream().filter(h -> h.getUsername().equals(idHost)).findFirst().orElse(null);

            if (host != null) {
                host.setTransientParams(); //resetto campi non persistenti
                addNotifAndStays(host); //carico notifiche e alloggi aggiornati
            }

            return host;
        } catch (Exception e) {
            throw new DAOException("Error in selectHost", e, GENERIC);
        }
    }

    @Override
    public Host selectHost(String username, String password) throws DAOException {
        try {
            //filtro per username e pass
            Host host = MemoryDatabase.getHosts().stream().filter(h -> h.getUsername().equals(username) && h.getPassword().equals(password)).findFirst().orElse(null);

            if (host != null) {
                host.setTransientParams();//resetto campi non persistenti
                addNotifAndStays(host); //carico notifiche e alloggi aggiornati
            }

            return host;
        } catch (Exception e) {
            throw new DAOException("Error in selectHost", e, GENERIC);
        }
    }

    @Override
    public void insertHost(Host host) throws DAOException {
        try {
            //verifico unicità
            boolean usernameExists = MemoryDatabase.getHosts().stream().anyMatch(h -> h.getUsername().equals(host.getUsername()));
            if (usernameExists) {
                throw new DAOException("Username already exists", DUPLICATE);
            }

            boolean emailExists = MemoryDatabase.getHosts().stream().anyMatch(h -> h.getEmailAddress().equals(host.getEmailAddress()));
            if (emailExists) {
                throw new DAOException("Host already exists", DUPLICATE);
            }
            //aggiungo alla lista
            MemoryDatabase.getHosts().add(host);
        } catch (Exception e) {
            throw new DAOException("Error in insertHost", e, GENERIC);
        }
    }
    //popolo liste di notifiche e alloggi dentro host
    private void addNotifAndStays(Host host) throws DAOException {
        if (host == null) return;

        try {
            //trovo alloggi dell'host
            List<Stay> stays = MemoryDatabase.getStays().stream().filter(s -> s.getHostUsername().equals(host.getUsername())).toList();

            //trovo le notifiche dell'host
            List<Notification> notifications = MemoryDatabase.getNotifications().stream().filter(n -> stays.stream().anyMatch(s -> s.getName().equals(n.getNameStay()))).toList();

            //aggiungo le liste all'host
            host.addStay(stays);
            host.addNotification(notifications);
        } catch (Exception e) {
            throw new DAOException("Error in addNotifAndStays", e, GENERIC);
        }
    }
}
