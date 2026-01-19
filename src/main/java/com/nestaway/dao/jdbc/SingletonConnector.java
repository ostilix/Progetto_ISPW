package com.nestaway.dao.jdbc;

import com.nestaway.exception.dao.ConnectionException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

//un solo punto di accesso per la connessione al DB
public class SingletonConnector {
    //static per mantenere l'unica istanza della connessione
    private static Connection connection = null;
    //credenziali del db
    private static final String CONNECTION_SETTINGS = "src/main/resources/properties/db.properties";
    //privato perche nessuno possa fare new SingletonConnector()
    private SingletonConnector() {}

    //se due thread chiedono la connessione, uno aspetta
    public static synchronized Connection getConnection() throws ConnectionException {
        if (connection == null) {
            //apro il file properties di configurazione
            try (InputStream input = new FileInputStream(CONNECTION_SETTINGS)) {
                Properties properties = new Properties();
                properties.load(input);

                //leggo come configurare la connessione
                String connectionUrl = properties.getProperty("CONNECTION_URL");
                String user = properties.getProperty("USER");
                String pass = properties.getProperty("PASS");

                //creo la connessione tramite driver
                connection = DriverManager.getConnection(connectionUrl, user, pass);
            } catch (IOException | SQLException e) {
                throw new ConnectionException("Error in getConnection: " + e.getMessage(), e);
            }
        }
        return connection;
    }

}
