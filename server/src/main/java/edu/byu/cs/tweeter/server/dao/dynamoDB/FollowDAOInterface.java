package edu.byu.cs.tweeter.server.dao.dynamoDB;

import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.FollowDyanmoDBBean;

public interface FollowDAOInterface {
    void createRelationship(String follower_handle, String followee_handle);
    void removeRelationship(String follower_handle, String followee_handle);
    boolean isFollower(String follower_handle, String followee_handle);
    DataPage<FollowDyanmoDBBean> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias);
    DataPage<FollowDyanmoDBBean> getPageOfFollowees(String targetUserAlias, int pageSize, String lastUserAlias);
}
