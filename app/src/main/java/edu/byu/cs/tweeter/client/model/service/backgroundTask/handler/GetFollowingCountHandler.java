package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.observer.FollowService;

public class GetFollowingCountHandler extends BackgroundTaskHandler<FollowService.GetFollowingAndFollowersObserver> {
    public GetFollowingCountHandler(FollowService.GetFollowingAndFollowersObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowService.GetFollowingAndFollowersObserver observer, Bundle data) {
        int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
        observer.setFollowingCount(count);
    }
}
