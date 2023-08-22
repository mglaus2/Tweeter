package edu.byu.cs.tweeter.client.model.service.observer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFeedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetStoryHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PostStatusHandler;
import edu.byu.cs.tweeter.client.presenter.GetStoryPresenter;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {
    private ExecuteService executeService;

    public StatusService() {
        executeService = new ExecuteService();
    }

    public interface Observer extends PagesObserver<Status> {}

    public interface PostStatusObserver extends ServiceObserver {
        void postStatus();
    }

    public void loadFeed(User user, int pageSize, Status lastStatus, Observer observer) {
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new GetFeedHandler(observer));
        executeService.executeTask(getFeedTask);
    }

    public void postStatus(Status newStatus, PostStatusObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
                newStatus, new PostStatusHandler(observer));
        executeService.executeTask(statusTask);
    }

    public void loadStory(User user, int pageSize, Status lastStatus, Observer observer) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new GetStoryHandler(observer));
        executeService.executeTask(getStoryTask);
    }
}
