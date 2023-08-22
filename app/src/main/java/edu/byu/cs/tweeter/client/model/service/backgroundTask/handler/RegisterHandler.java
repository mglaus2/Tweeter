package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.observer.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterHandler extends BackgroundTaskHandler<UserService.Observer> {
    public RegisterHandler(UserService.Observer observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserService.Observer observer, Bundle data) {
        User registeredUser = (User) data.getSerializable(RegisterTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);

        Cache.getInstance().setCurrUser(registeredUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.handleSuccess(registeredUser);
    }
}
