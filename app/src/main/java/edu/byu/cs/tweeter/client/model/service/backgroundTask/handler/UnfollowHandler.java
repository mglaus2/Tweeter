package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import edu.byu.cs.tweeter.client.model.service.observer.FollowService;

public class UnfollowHandler extends BackgroundTaskHandler<FollowService.UpdateFollowObserver> {
    public UnfollowHandler(FollowService.UpdateFollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowService.UpdateFollowObserver observer, Bundle data) {
        observer.updateFollowStatus(true);
    }
}
