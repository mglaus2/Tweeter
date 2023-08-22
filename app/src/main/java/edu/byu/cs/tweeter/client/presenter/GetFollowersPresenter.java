package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.observer.FollowService;
import edu.byu.cs.tweeter.client.model.service.observer.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter extends PagedPresenter<User> {
    public interface View extends PagedView<User> {}

    public GetFollowersPresenter(View view) {
        super(view);
    }

    @Override
    protected void loadItems(User user, int PAGE_SIZE, User lastItem) {
        followService.loadMoreFollowers(user, PAGE_SIZE, lastItem, new GetFollowerObserver());
    }

    public void loadMoreFollowers(User user) {
        loadUsersItems(user);
    }

    public void getUser(String userAlias) {
        userService.getUser(userAlias, new GetUserObserver());
    }


    private class GetFollowerObserver implements FollowService.Observer {
        @Override
        public void handleFailure(String message) {
            setLoading(false);
            view.displayMessage("Failed to get followers: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            setLoading(false);
            view.displayMessage(exception.getMessage());
        }

        @Override
        public void addItems(List<User> items, boolean hasMorePages) {
            addItemsToView(items, hasMorePages);
        }
    }

    private class GetUserObserver implements UserService.Observer {
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get user's followers: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get user's followers because of exception: " + exception.getMessage());
        }

        @Override
        public void handleSuccess(User data) {
            ((View)view).displayInfo(data);
        }
    }
}