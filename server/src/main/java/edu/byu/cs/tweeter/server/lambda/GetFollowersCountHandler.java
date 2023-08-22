package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersCountHandler implements RequestHandler<FollowersCountRequest, FollowersCountResponse> {

    @Override
    public FollowersCountResponse handleRequest(FollowersCountRequest request, Context context) {
        DAOFactoryInterface daoFactory = new DAOFactory();
        FollowService service = new FollowService(daoFactory);
        return service.getFollowersCount(request);
    }
}
