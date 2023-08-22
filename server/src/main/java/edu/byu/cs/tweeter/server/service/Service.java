package edu.byu.cs.tweeter.server.service;

import java.sql.DatabaseMetaData;

import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.AuthtokenDyanmoDBBean;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DataPage;

public class Service {
    DAOFactoryInterface daoFactory;

    public Service(DAOFactoryInterface daoFactory) {
        this.daoFactory = daoFactory;
    }

    public boolean validateAuthtoken(String token) {
        long expirationTime = daoFactory.getAuthtokenDAO().getAuthtokenExpirationTime(token);
        if(expirationTime == -1) {
            System.out.println("Authtoken was not found");
            return false;
        }

        long currTime = System.currentTimeMillis();
        if(currTime >= expirationTime) {
            System.out.println("Authtoken is expired");
            daoFactory.getAuthtokenDAO().deleteAuthtoken(token);
            return false;
        }

        daoFactory.getAuthtokenDAO().updateAuthtokenExpirationTime(token);
        return true;
    }
}
