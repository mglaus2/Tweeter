package edu.byu.cs.tweeter.model.net.request;

import java.util.List;

public class UpdateFeedMessage {
    private String authorAlias;
    private Long timestamp;
    private String post;
    private List<String> followerAliases;

    public UpdateFeedMessage() {}

    public UpdateFeedMessage(String authorAlias, Long timestamp, String post, List<String> followerAliases) {
        this.authorAlias = authorAlias;
        this.timestamp = timestamp;
        this.post = post;
        this.followerAliases = followerAliases;
    }

    public String getAuthorAlias() {
        return authorAlias;
    }

    public void setAuthorAlias(String authorAlias) {
        this.authorAlias = authorAlias;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public List<String> getFollowerAliases() {
        return followerAliases;
    }

    public void setFollowerAliases(List<String> followerAliases) {
        this.followerAliases = followerAliases;
    }
}
