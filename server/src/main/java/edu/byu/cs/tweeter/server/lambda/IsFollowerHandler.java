package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.FollowService;

public class IsFollowerHandler implements RequestHandler<IsFollowerRequest, IsFollowerResponse> {
  @Override
  public IsFollowerResponse handleRequest(IsFollowerRequest request, Context context) {
      DAOFactoryInterface daoFactory = new DAOFactory();
    FollowService service = new FollowService(daoFactory);
    return service.isFollower(request);
  }
}
