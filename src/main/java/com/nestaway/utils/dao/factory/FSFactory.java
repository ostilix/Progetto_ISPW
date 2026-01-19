package com.nestaway.utils.dao.factory;

import com.nestaway.dao.*;
import com.nestaway.dao.fs.*;

//concrete factory, comunico tramite file CSV
public class FSFactory extends FactorySingletonDAO {
    //creo e restituisco implementazione FS dei DAO
    public AvailabilityDAO getAvailabilityDAO(){
        return new AvailabilityFS();
    }
    public BookingDAO getBookingDAO(){
        return new BookingFS();
    }
    public HostDAO getHostDAO(){
        return new HostFS();
    }
    public NotificationDAO getNotificationDAO(){
        return new NotificationFS();
    }
    public ReviewDAO getReviewDAO(){
        return new ReviewFS();
    }
    public StayDAO getStayDAO(){
        return new StayFS();
    }
}
