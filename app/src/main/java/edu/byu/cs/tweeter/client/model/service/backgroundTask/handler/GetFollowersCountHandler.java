package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.observer.FollowService;

public class GetFollowersCountHandler extends BackgroundTaskHandler<FollowService.GetFollowingAndFollowersObserver> {
    public GetFollowersCountHandler(FollowService.GetFollowingAndFollowersObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowService.GetFollowingAndFollowersObserver observer, Bundle data) {
        int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
        observer.setFollowersCount(count);
    }
}
