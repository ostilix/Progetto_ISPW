package com.nestaway.dao.demo;

import com.nestaway.dao.NotificationDAO;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Notification;
import com.nestaway.model.Stay;
import com.nestaway.utils.dao.MemoryDatabase;

import java.util.List;

import static com.nestaway.exception.dao.TypeDAOException.*;
//implementazione dell'interfaccia DAO
public class NotificationDEMO implements NotificationDAO {

    @Override
    public List<Notification> selectNotifications(String idHost) throws DAOException {
        try {
            return MemoryDatabase.getNotifications().stream().filter(n -> {
                //cerco lo stay collegato alla notifica
                Stay stay = MemoryDatabase.getStays().stream().filter(s -> s.getName().equals(n.getNameStay())).findFirst().orElse(null);
                //verifico se esiste e se appartiene all'host
                return stay != null && stay.getHostUsername().equals(idHost);
            }).toList();
        } catch (Exception e) {
            throw new DAOException("Error in selectNotifications", e, GENERIC);
        }
    }

    @Override
    public void addNotification(String idHost, Notification notification) throws DAOException {
        try {
            //controllo duplicati
            boolean exists = MemoryDatabase.getNotifications().stream().anyMatch(n -> n.getType() == notification.getType() && n.getNameStay().equals(notification.getNameStay()) && n.getBookingCode().equals(notification.getBookingCode()) && n.getDateAndTime().equals(notification.getDateAndTime()));

            if (exists) {
                throw new DAOException("Notification already exists", DUPLICATE);
            }

            MemoryDatabase.getNotifications().add(notification);
        } catch (Exception e) {
            throw new DAOException("Error in addNotification", e, GENERIC);
        }
    }

    @Override
    public void deleteNotification(String idHost, List<Notification> notifsToDelete) throws DAOException {
        try {
            MemoryDatabase.getNotifications().removeIf(n -> {
                Stay stay = MemoryDatabase.getStays().stream().filter(s -> s.getName().equals(n.getNameStay())).findFirst().orElse(null);

                //controllo che non stia eliminando notifiche di altri host
                if (stay == null || !stay.getHostUsername().equals(idHost)) {
                    return false;
                }
                //controllo che la notifica esista
                return notifsToDelete.stream().anyMatch(del -> del.getType() == n.getType() && del.getNameStay().equals(n.getNameStay()) && del.getBookingCode().equals(n.getBookingCode()) && del.getDateAndTime().equals(n.getDateAndTime()));
            });
        } catch (Exception e) {
            throw new DAOException("Error in deleteNotification", e, GENERIC);
        }
    }


    @Override
    public void deleteNotificationByHost(String idHost) throws DAOException {
        try {
            //rimuovo notifiche che appartengono a stay di quell'host
            MemoryDatabase.getNotifications().removeIf(n -> {
                Stay stay = MemoryDatabase.getStays().stream().filter(s -> s.getName().equals(n.getNameStay())).findFirst().orElse(null);
                return stay != null && stay.getHostUsername().equals(idHost);
            });
        } catch (Exception e) {
            throw new DAOException("Error in deleteNotificationByHost", e, GENERIC);
        }
    }
}
