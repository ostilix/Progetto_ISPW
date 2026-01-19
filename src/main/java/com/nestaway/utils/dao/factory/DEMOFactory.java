package com.nestaway.utils.dao.factory;

import com.nestaway.dao.*;
import com.nestaway.dao.demo.*;

//concrete factory
public class DEMOFactory extends FactorySingletonDAO {
    //creo e restituisco implementazione DEMO dei DAO
    public AvailabilityDAO getAvailabilityDAO(){
        return new AvailabilityDEMO();
    }
    public BookingDAO getBookingDAO(){
        return new BookingDEMO();
    }
    public HostDAO getHostDAO(){
        return new HostDEMO();
    }
    public NotificationDAO getNotificationDAO() { return new NotificationDEMO(); }
    public ReviewDAO getReviewDAO(){
        return new ReviewDEMO();
    }
    public StayDAO getStayDAO(){
        return new StayDEMO();
    }
}
