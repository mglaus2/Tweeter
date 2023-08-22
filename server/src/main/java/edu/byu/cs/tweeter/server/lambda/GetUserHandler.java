package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.UserService;

public class GetUserHandler implements RequestHandler<UserRequest, UserResponse> {
  @Override
  public UserResponse handleRequest(UserRequest request, Context context) {
      DAOFactoryInterface daoFactory = new DAOFactory();
      UserService userService = new UserService(daoFactory);
      return userService.getUser(request);
  }
}
