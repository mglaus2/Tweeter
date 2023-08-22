package edu.byu.cs.tweeter.server.dao.dynamoDB;

import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.StoryDyanmoDBBean;

public interface StoryDAOInterface {
    void createPost(String authorAlias, long timestamp, String post);
    DataPage<StoryDyanmoDBBean> getStory(String userAlias, int pageSize, Long lastTimeStamp);
}
