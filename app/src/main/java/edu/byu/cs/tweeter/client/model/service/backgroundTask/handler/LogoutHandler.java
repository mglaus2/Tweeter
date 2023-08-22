package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import edu.byu.cs.tweeter.client.model.service.observer.UserService;

public class LogoutHandler extends BackgroundTaskHandler<UserService.Observer> {
    public LogoutHandler(UserService.Observer observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserService.Observer observer, Bundle data) {
        observer.handleSuccess(null);
    }
}
