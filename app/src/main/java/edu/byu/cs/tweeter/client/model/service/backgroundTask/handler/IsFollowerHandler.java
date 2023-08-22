package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.observer.FollowService;

public class IsFollowerHandler extends BackgroundTaskHandler<FollowService.IsFollowerObserver> {
    public IsFollowerHandler(FollowService.IsFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowService.IsFollowerObserver observer, Bundle data) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.isFollower(isFollower);
    }
}
