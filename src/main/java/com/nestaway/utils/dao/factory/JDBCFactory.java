package com.nestaway.utils.dao.factory;

import com.nestaway.dao.*;
import com.nestaway.dao.jdbc.*;

//concrete factory, serve per comunicare con il DB
public class JDBCFactory extends FactorySingletonDAO {
    //creo e restituisco implementazione JDBC dei DAO
    public AvailabilityDAO getAvailabilityDAO(){
        return new AvailabilityJDBC();
    }
    public BookingDAO getBookingDAO(){
        return new BookingJDBC();
    }
    public HostDAO getHostDAO(){
        return new HostJDBC();
    }
    public NotificationDAO getNotificationDAO(){
        return new NotificationJDBC();
    }
    public ReviewDAO getReviewDAO(){
        return new ReviewJDBC();
    }
    public StayDAO getStayDAO(){
        return new StayJDBC();
    }

}
