package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() throws IOException, TweeterRemoteException {
      FeedRequest feedRequest = new FeedRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), getLastItem());
      FeedResponse feedResponse = serverFacade.getFeed(feedRequest, "/getfeed");
        if(!feedResponse.isSuccess()) {
            sendFailedMessage(feedResponse.getMessage());
            return null;
        }
      return new Pair<>(feedResponse.getStatuses(), feedResponse.getHasMorePages());
    }
}
