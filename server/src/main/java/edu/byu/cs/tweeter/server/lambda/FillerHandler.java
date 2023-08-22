package edu.byu.cs.tweeter.server.lambda;

import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.Filler;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.UserService;

public class FillerHandler {
    public void handleRequest() {
        Filler filler = new Filler();
        filler.fillDatabase();
    }
}
