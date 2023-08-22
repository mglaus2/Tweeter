package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.observer.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryHandler extends BackgroundTaskHandler<StatusService.Observer> {
    public GetStoryHandler(StatusService.Observer observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(StatusService.Observer observer, Bundle data) {
        List<Status> statuses = (List<Status>) data.getSerializable(GetStoryTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetStoryTask.MORE_PAGES_KEY);
        observer.addItems(statuses, hasMorePages);
    }
}
