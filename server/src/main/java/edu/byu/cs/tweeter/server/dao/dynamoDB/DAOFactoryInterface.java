package edu.byu.cs.tweeter.server.dao.dynamoDB;

public interface DAOFactoryInterface {
    AuthtokenDAOInterface getAuthtokenDAO();
    FeedDAOInterface getFeedDAO();
    FollowDAOInterface getFollowDAO();
    StoryDAOInterface getStoryDAO();
    UserDAOInterface getUserDAO();
}
