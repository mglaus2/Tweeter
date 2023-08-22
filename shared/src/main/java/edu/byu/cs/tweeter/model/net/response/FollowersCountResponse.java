package edu.byu.cs.tweeter.model.net.response;

public class FollowersCountResponse extends Response {
    private int followerCount;

    public FollowersCountResponse(String message) {
        super(false, message);
    }

    public FollowersCountResponse(int followerCount) {
        super(true, null);
        this.followerCount = followerCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }
}
