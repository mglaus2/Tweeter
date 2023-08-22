package edu.byu.cs.tweeter.server.dao.dynamoDB;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.AuthtokenDyanmoDBBean;

public interface AuthtokenDAOInterface {
    AuthToken createAuthtoken(String alias);
    void deleteAuthtoken(String auth_token);
    String getCurrUserAlias(String token);
    long getAuthtokenExpirationTime(String token);
    void updateAuthtokenExpirationTime(String token);
}
