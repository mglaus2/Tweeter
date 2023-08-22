package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() throws IOException, TweeterRemoteException {
      FollowersRequest followersRequest;
      if(getLastItem() == null) {
        followersRequest = new FollowersRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), null);
      }
      else {
        followersRequest = new FollowersRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), getLastItem().getAlias());
      }
      FollowersResponse followersResponse = serverFacade.getFollowers(followersRequest, "getfollowers");

      if(followersResponse.isSuccess()) {
          return new Pair<>(followersResponse.getFollowers(), followersResponse.getHasMorePages());
      }
      else {
          sendFailedMessage(followersResponse.getMessage());
          return null;
      }
    }
}
