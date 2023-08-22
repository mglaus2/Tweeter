package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() throws IOException, TweeterRemoteException {
      FollowingCountRequest followingCountRequest = new FollowingCountRequest(getAuthToken(), getTargetUser());
      FollowingCountResponse followingCountResponse = serverFacade.getFollowingCount(followingCountRequest, "/getfollowingcount");
        if(!followingCountResponse.isSuccess()) {
            sendFailedMessage(followingCountResponse.getMessage());
            return -1;
        }
      return followingCountResponse.getFolloweeCount();
    }
}
