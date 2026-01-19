package com.nestaway.dao;

import com.nestaway.exception.dao.DAOException;
import com.nestaway.model.Host;

public interface HostDAO {
    Host selectHost(String idHost) throws DAOException;
    Host selectHost(String username, String password) throws DAOException;
    void insertHost(Host host) throws DAOException;
}
