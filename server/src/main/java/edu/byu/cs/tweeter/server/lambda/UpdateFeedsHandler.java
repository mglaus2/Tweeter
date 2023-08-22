package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.StatusService;

public class UpdateFeedsHandler implements RequestHandler<SQSEvent, Void>  {
    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        DAOFactoryInterface daoFactory = new DAOFactory();
        StatusService service = new StatusService(daoFactory);
        service.updateFeeds(sqsEvent);
        return null;
    }
}
