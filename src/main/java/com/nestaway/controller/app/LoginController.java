package com.nestaway.controller.app;

import com.nestaway.bean.HostBean;
import com.nestaway.bean.UserBean;
import com.nestaway.exception.*;
import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Host;
import com.nestaway.model.User;
import com.nestaway.utils.ToBeanConverter;
import com.nestaway.utils.dao.factory.FactorySingletonDAO;

import static com.nestaway.exception.dao.TypeDAOException.DUPLICATE;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    public UserBean login(UserBean userBean) throws OperationFailedException, NotFoundException {
        try{
            //creo Model User
            User user = new User(userBean.getUsername(), userBean.getPassword());
            //tramite DAO cerco l'host con quelle credenziali
            Host host = FactorySingletonDAO. getDefaultDAO().getHostDAO().selectHost(user.getUsername(), user.getPassword());
            if (host == null) {
                throw new NotFoundException("User not found.");
            }
            //converto Model Host in Bean Host per la GUI
            return ToBeanConverter.fromHostToHostBean(host);
        } catch (DAOException e) {
            Logger.getGlobal().log(Level.WARNING, e.getMessage(), e.getCause());
            throw new OperationFailedException();
        } catch (IncorrectDataException | EncryptionException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e.getCause());
            throw new OperationFailedException();
        }
    }

    public UserBean register(HostBean hostBean) throws OperationFailedException, DuplicateEntryException {
        try{
            //creo Model Host dai dati del Bean
            Host host = new Host(hostBean.getFirstName(), hostBean.getLastName(), hostBean.getEmailAddress(), hostBean.getUsername(), hostBean.getInfoPayPal(), hostBean.getPassword());

            //inserico l'host nel DB
            FactorySingletonDAO.getDefaultDAO().getHostDAO().insertHost(host);
            return ToBeanConverter.fromHostToHostBean(host);
        } catch (DAOException e) {
            if (e.getTypeException().equals(DUPLICATE)) {
                throw new DuplicateEntryException(e.getMessage());
            } else {
                Logger.getGlobal().log(Level.WARNING, e.getMessage(), e.getCause());
                throw new OperationFailedException();
            }
        } catch (IncorrectDataException | EncryptionException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e.getCause());
            throw new OperationFailedException();
        }
    }
}

