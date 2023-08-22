package edu.byu.cs.tweeter.server.dao.dynamoDB;

import edu.byu.cs.tweeter.model.domain.User;

public interface UserDAOInterface {
    User registerUser(String firstName, String lastName, String alias, String hashedPassword, String imageURL);
    User getUser(String alias);
    String getHashedPassword(String alias);
    int getFollowingCount(String alias);
    boolean updateFollowingCount(String alias, int followingCount);
    int getFollowersCount(String alias);
    boolean updateFollowersCount(String alias, int followersCount);
}
