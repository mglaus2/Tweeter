package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.FollowService;

public class UnfollowHandler implements RequestHandler<UnfollowRequest, UnfollowResponse> {
  @Override
  public UnfollowResponse handleRequest(UnfollowRequest request, Context context) {
      DAOFactoryInterface daoFactory = new DAOFactory();
    FollowService service = new FollowService(daoFactory);
    return service.unfollow(request);
  }
}
