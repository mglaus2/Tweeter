package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.observer.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFeedPresenter extends PagedPresenter<Status> {
    public interface View extends PagedView<Status> {}

    public GetFeedPresenter(View view) {
        super(view);
    }

    @Override
    protected void loadItems(User user, int PAGE_SIZE, Status lastItem) {
        statusService.loadFeed(user, PAGE_SIZE, lastItem, new GetFeedObserver());
    }

    public void loadFeed(User user) {
        loadUsersItems(user);
    }

    public void getUser(String userAlias) {
        userService.getUser(userAlias, new GetUserObserver());
    }

    private class GetFeedObserver implements StatusService.Observer {
        @Override
        public void handleFailure(String message) {
            setLoading(false);
            view.displayMessage("Failed to get feed: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            setLoading(false);
            view.displayMessage("Failed to get feed because of exception: " + exception.getMessage());
        }

        @Override
        public void addItems(List<Status> items, boolean hasMorePages) {
            addItemsToView(items, hasMorePages);
        }
    }

    private class GetUserObserver implements UserService.Observer {
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get user's feed: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get user's feed because of exception: " + exception.getMessage());
        }

        @Override
        public void handleSuccess(User data) {
            ((View)view).displayInfo(data);
        }
    }
}
