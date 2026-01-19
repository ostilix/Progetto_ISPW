package com.nestaway.dao;

import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Notification;

import java.util.List;

public interface NotificationDAO {
    List<Notification> selectNotifications(String idHost) throws DAOException;
    void addNotification(String idHost, Notification notification) throws DAOException;
    void deleteNotification(String idHost, List<Notification> notification) throws DAOException;
    void deleteNotificationByHost(String idHost) throws DAOException;
}
