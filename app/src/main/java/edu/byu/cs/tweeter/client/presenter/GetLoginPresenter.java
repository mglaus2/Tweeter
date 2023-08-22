package edu.byu.cs.tweeter.client.presenter;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.observer.UserService;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;

public class GetLoginPresenter extends Presenter {
    public interface View extends Presenter.View {
        void displayLoggedInUser(User loggedInUser);
    }

    public GetLoginPresenter(View view) {
        super(view);
    }

    public void loginUser(String alias, String password) throws IOException, TweeterRemoteException {
        userService.loginUser(alias, password, new LoginObserver());
    }

    public void validateLogin(String alias, String password) {
        if (alias.length() > 0 && alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    private class LoginObserver implements UserService.Observer {
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to login: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get login because of exception: " + exception.getMessage());
        }

        @Override
        public void handleSuccess(User data) {
            ((View)view).displayLoggedInUser(data);
        }
    }
}
