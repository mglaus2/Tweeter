package edu.byu.cs.tweeter.server.dao.dynamoDB;

import edu.byu.cs.tweeter.model.net.request.UpdateFeedMessage;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.FeedDyanmoDBBean;

public interface FeedDAOInterface {
    void createFeedItem(String alias, long timestamp, String authorAlias, String post);
    DataPage<FeedDyanmoDBBean> getFeed(String alias, int limit, Long lastTimeStamp);
    void createBatchFeeds(UpdateFeedMessage updateFeedMessage);
}
