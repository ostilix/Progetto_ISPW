package com.nestaway.utils.dao.factory;

import com.nestaway.dao.*;

//abstract factory e singleton
public abstract class FactorySingletonDAO {

    //instanzio il singleton
    protected static FactorySingletonDAO instance = null;

    protected FactorySingletonDAO() {
    }

    public static synchronized FactorySingletonDAO getDefaultDAO() {
        //controllo se l'istanza esiste già
        if (instance == null) {
            //leggo il tipo di DAO che voglio usare
            String daoType = System.getProperty("DAO_TYPE");
            switch (TypeDAO.valueOf(daoType)) {
                case JDBC:
                    instance = new JDBCFactory();
                    break;
                case FS:
                    instance = new FSFactory();
                    break;
                case DEMO:
                    instance = new DEMOFactory();
                    break;
            }
            //ritorno l'istanza appena creata
            return instance;
        }
        //se esisteva già, la ritorno
        return instance;
    }

    public abstract AvailabilityDAO getAvailabilityDAO();

    public abstract BookingDAO getBookingDAO();

    public abstract HostDAO getHostDAO();

    public abstract NotificationDAO getNotificationDAO();

    public abstract ReviewDAO getReviewDAO();

    public abstract StayDAO getStayDAO();
}
