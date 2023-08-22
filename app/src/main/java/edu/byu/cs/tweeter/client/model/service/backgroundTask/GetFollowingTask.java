package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollowee, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() throws IOException, TweeterRemoteException {
        FollowingRequest followingRequest;
        if(getLastItem() == null) {
            followingRequest = new FollowingRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), null);
        }
        else {
            followingRequest = new FollowingRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), getLastItem().getAlias());
        }
        FollowingResponse followingResponse = serverFacade.getFollowees(followingRequest, "/getfollowing");
        if(!followingResponse.isSuccess()) {
            sendFailedMessage(followingResponse.getMessage());
            return null;
        }
        return new Pair<>(followingResponse.getFollowees(), followingResponse.getHasMorePages());
    }
}
