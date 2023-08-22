package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowRequest {
  private AuthToken authToken;
  private User followee;

  private UnfollowRequest() {}

  public UnfollowRequest(User followee, AuthToken authToken) {
    this.followee = followee;
    this.authToken = authToken;
  }

  public AuthToken getAuthToken() {
    return authToken;
  }

  public void setAuthToken(AuthToken authToken) {
    this.authToken = authToken;
  }

  public User getFollowee() {
    return followee;
  }

  public void setFollowee(User followee) {
    this.followee = followee;
  }
}
