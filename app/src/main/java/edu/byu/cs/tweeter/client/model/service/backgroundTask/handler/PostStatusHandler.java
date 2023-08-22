package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import edu.byu.cs.tweeter.client.model.service.observer.StatusService;

public class PostStatusHandler extends BackgroundTaskHandler<StatusService.PostStatusObserver> {
    public PostStatusHandler(StatusService.PostStatusObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(StatusService.PostStatusObserver observer, Bundle data) {
        observer.postStatus();
    }
}
