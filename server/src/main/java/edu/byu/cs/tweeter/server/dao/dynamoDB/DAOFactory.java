package edu.byu.cs.tweeter.server.dao.dynamoDB;

public class DAOFactory implements DAOFactoryInterface {
    @Override
    public AuthtokenDAOInterface getAuthtokenDAO() {
        return new AuthtokenDAO();
    }

    @Override
    public FeedDAOInterface getFeedDAO() {
        return new FeedDAO();
    }

    @Override
    public FollowDAOInterface getFollowDAO() {
        return new FollowDAO();
    }

    @Override
    public StoryDAOInterface getStoryDAO() {
        return new StoryDAO();
    }

    @Override
    public UserDAOInterface getUserDAO() {
        return new UserDAO();
    }
}
