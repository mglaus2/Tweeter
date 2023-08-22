package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.observer.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersHandler extends BackgroundTaskHandler<FollowService.Observer> {
    public GetFollowersHandler(FollowService.Observer observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowService.Observer observer, Bundle data) {
        List<User> followers = (List<User>) data.getSerializable(GetFollowersTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFollowersTask.MORE_PAGES_KEY);
        observer.addItems(followers, hasMorePages);
    }
}
