package com.nestaway.controller.app;

import com.nestaway.bean.HostBean;
import com.nestaway.bean.UserBean;
import com.nestaway.exception.IncorrectDataException;
import com.nestaway.exception.NotFoundException;
import com.nestaway.exception.OperationFailedException;
import com.nestaway.exception.DuplicateEntryException;
import com.nestaway.utils.dao.factory.FactorySingletonDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    @BeforeAll
    static void setup() throws Exception {
        Field instance = FactorySingletonDAO.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        System.setProperty("DAO_TYPE", "JDBC");
    }

    @Test
    void testLogin() throws IncorrectDataException, OperationFailedException, NotFoundException {
        LoginController loginController = new LoginController();
        UserBean userBean = new UserBean();
        userBean.setUsername("mariorossi");
        userBean.setPassword("mariorossi");
        UserBean user = loginController.login(userBean);
        assertEquals("mariorossi", user.getUsername());
    }

    @Test
    void testRegister() throws IncorrectDataException, OperationFailedException, DuplicateEntryException{
        HostBean hostBean = new HostBean();
        int randomId = new Random().nextInt(10000);
        String username = "pieroneri" + randomId;
        String email = "piero.neri" + randomId + "@gmail.com";
        hostBean.setUsername(username);
        hostBean.setPassword("pieroneri");
        hostBean.setEmailAddress(email);
        hostBean.setFirstName("Piero");
        hostBean.setLastName("Nero");
        hostBean.setInfoPayPal(email);
        LoginController loginController = new LoginController();
        UserBean user = loginController.register(hostBean);
        assertEquals("pieroneri", user.getUsername());
        assertNotEquals("pieroneri", user.getPassword());
    }
}
