package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.observer.FollowService;
import edu.byu.cs.tweeter.client.model.service.observer.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.UserService;

public abstract class Presenter {
    public interface View {
        void displayMessage(String message);
    }

    protected View view;
    protected StatusService statusService;
    protected UserService userService;
    protected FollowService followService;

    public Presenter(View view) {
        this.view = view;
        statusService = new StatusService();
        userService = new UserService();
        followService = new FollowService();
    }
}
