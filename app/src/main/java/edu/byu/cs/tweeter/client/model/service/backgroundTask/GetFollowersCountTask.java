package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() throws IOException, TweeterRemoteException {
        FollowersCountRequest followersCountRequest = new FollowersCountRequest(getAuthToken(), getTargetUser());
        FollowersCountResponse followersCountResponse = serverFacade.getFollowersCount(followersCountRequest, "/getfollowerscount");
        if(!followersCountResponse.isSuccess()) {
            sendFailedMessage(followersCountResponse.getMessage());
            return -1;
        }
        return followersCountResponse.getFollowerCount();
    }
}
