package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {
  @Override
  public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
      DAOFactoryInterface daoFactory = new DAOFactory();
    StatusService service = new StatusService(daoFactory);
    return service.postStatus(request);
  }
}
