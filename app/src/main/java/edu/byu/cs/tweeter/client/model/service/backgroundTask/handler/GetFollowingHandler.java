package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.observer.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingHandler extends BackgroundTaskHandler<FollowService.Observer> {
    public GetFollowingHandler(FollowService.Observer observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowService.Observer observer, Bundle data) {
        List<User> followees = (List<User>) data.getSerializable(GetFollowingTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
        observer.addItems(followees, hasMorePages);
    }
}
