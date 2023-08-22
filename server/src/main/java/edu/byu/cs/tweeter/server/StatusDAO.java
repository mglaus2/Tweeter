package edu.byu.cs.tweeter.server;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusDAO {

  public StoryResponse getStory(StoryRequest request) {
    // TODO: Generates dummy data. Replace with a real implementation.
    assert request.getLimit() > 0;
    assert request.getTargetUserAlias() != null;

    List<Status> allStatuses = getDummyStatuses();
    List<Status> responseStatuses= new ArrayList<>(request.getLimit());

    boolean hasMorePages = false;

    if(request.getLimit() > 0) {
      if (allStatuses != null) {
        int storyIndex = getStoryStartingIndex(request.getLastStatus(), allStatuses);

        for(int limitCounter = 0; storyIndex < allStatuses.size() && limitCounter < request.getLimit(); storyIndex++, limitCounter++) {
          responseStatuses.add(allStatuses.get(storyIndex));
        }

        hasMorePages = storyIndex < allStatuses.size();
      }
    }

    return new StoryResponse(responseStatuses, hasMorePages);
  }

  public FeedResponse getFeed(FeedRequest request) {
    // TODO: Generates dummy data. Replace with a real implementation.
    assert request.getLimit() > 0;
    assert request.getTargetUserAlias() != null;

    List<Status> allStatuses = getDummyStatuses();
    List<Status> responseStatuses= new ArrayList<>(request.getLimit());

    boolean hasMorePages = false;

    if(request.getLimit() > 0) {
      if (allStatuses != null) {
        int storyIndex = getStoryStartingIndex(request.getLastStatus(), allStatuses);

        for(int limitCounter = 0; storyIndex < allStatuses.size() && limitCounter < request.getLimit(); storyIndex++, limitCounter++) {
          responseStatuses.add(allStatuses.get(storyIndex));
        }

        hasMorePages = storyIndex < allStatuses.size();
      }
    }

    return new FeedResponse(responseStatuses, hasMorePages);
  }

  private int getStoryStartingIndex(Status lastStatus, List<Status> allStatuses) {

    int storyIndex = 0;

    if(lastStatus != null) {
      // This is a paged request for something after the first page. Find the first item
      // we should return
      for (int i = 0; i < allStatuses.size(); i++) {
        if(lastStatus.equals(allStatuses.get(i))) {
          // We found the index of the last item returned last time. Increment to get
          // to the first one we should return
          storyIndex = i + 1;
          break;
        }
      }
    }

    return storyIndex;
  }

  List<Status> getDummyStatuses() {
    return getFakeData().getFakeStatuses();
  }

  FakeData getFakeData() {
    return FakeData.getInstance();
  }
}
