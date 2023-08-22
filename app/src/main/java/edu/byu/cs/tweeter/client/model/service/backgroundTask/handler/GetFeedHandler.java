package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.observer.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedHandler extends BackgroundTaskHandler<StatusService.Observer> {
    public GetFeedHandler(StatusService.Observer observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(StatusService.Observer observer, Bundle data) {
        List<Status> statuses = (List<Status>) data.getSerializable(GetFeedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFeedTask.MORE_PAGES_KEY);
        observer.addItems(statuses, hasMorePages);
    }
}
