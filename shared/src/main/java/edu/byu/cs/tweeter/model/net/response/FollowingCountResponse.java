package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;

public class FollowingCountResponse extends Response {
  private int followeeCount;

  public FollowingCountResponse(String message) {
    super(false, message);
  }

  public FollowingCountResponse(int followeeCount) {
    super(true, null);
    this.followeeCount = followeeCount;
  }

  public int getFolloweeCount() {
    return followeeCount;
  }
}
