package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.FollowService;
import edu.byu.cs.tweeter.client.model.service.observer.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetMainPresenter extends Presenter {
    private StatusService testStatusService;
    public interface View extends Presenter.View {
        void logoutUser();
        void updateFollowerCount(int count);
        void updateFollowingCount(int count);
        void userIsFollower();
        void userIsNotFollower();
        void updateFollowStatus();
        void postStatus();
    }

    protected StatusService getStatusService() {
        if(testStatusService == null) {
            testStatusService = new StatusService();
        }

        return testStatusService;
    }

    public GetMainPresenter(View view) {
        super(view);
    }

    public void logoutUser() {
        userService.logoutUser(new LogoutUserObserver());
    }

    public void updateSelectedUserFollowingAndFollowers(User selectedUser) {
        followService.updateSelectedUserFollowingAndFollowers(selectedUser, new GetFollowingAndFollowersCount());
    }

    public void isFollower(User selectedUser) {
        followService.isFollower(selectedUser, new IsFollowerObserver());
    }

    public void followSelectedUser(User selectedUser) {
        followService.followSelectedUser(selectedUser, new UpdateFollowObserver());
    }

    public void unfollowSelectedUser(User selectedUser) {
        followService.unfollowSelectedUser(selectedUser, new UpdateFollowObserver());
    }

    public void postStatus(String post) throws ParseException {
        Status newStatus = createNewStatus(post);
        getStatusService().postStatus(newStatus, new PostStatusObserver());
    }

    public Status createNewStatus(String post) throws ParseException {
        return new Status(post, Cache.getInstance().getCurrUser(), System.currentTimeMillis(), parseURLs(post), parseMentions(post));
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    private List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    private List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    private int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }


    private class GetFollowingAndFollowersCount implements FollowService.GetFollowingAndFollowersObserver {
        @Override
        public void setFollowingCount(int count) {
            ((View)view).updateFollowingCount(count);
        }

        @Override
        public void setFollowersCount(int count) {
            ((View)view).updateFollowerCount(count);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get followers count: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get followers count because of exception: " + exception.getMessage());
        }
    }

    private class LogoutUserObserver implements UserService.Observer {
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to logout: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get logout because of exception: " + exception.getMessage());
        }

        @Override
        public void handleSuccess(User data) {
            ((View)view).logoutUser();
        }
    }

    private class IsFollowerObserver implements FollowService.IsFollowerObserver {
        @Override
        public void isFollower(boolean isFollower) {
            // If logged in user if a follower of the selected user, display the follow button as "following"
            if (isFollower) {
                ((View)view).userIsFollower();
            } else {
                ((View)view).userIsNotFollower();
            }
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to determine following relationship because of exception: " + exception.getMessage());
        }
    }

    private class UpdateFollowObserver implements FollowService.UpdateFollowObserver {
        @Override
        public void updateFollowStatus(boolean value) {
            if(value) {
                ((View)view).userIsNotFollower();
            }
            else {
                ((View)view).userIsFollower();
            }
            ((View)view).updateFollowStatus();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to follow or unfollow: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to follow or unfollow because of exception: " + exception.getMessage());
        }
    }

    public class PostStatusObserver implements StatusService.PostStatusObserver {
        @Override
        public void postStatus() {
            ((View)view).postStatus();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to post status: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to post status because of exception: " + exception.getMessage());
        }
    }
}
