package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultDesktopManager;

import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.UserDyanmoDBBean;
import edu.byu.cs.tweeter.server.dao.dynamoDB.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.UserDAO;

public class Filler {
    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 10000;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@mglaus";

    public static void fillDatabase() {

        // Get instance of DAOs by way of the Abstract Factory Pattern
        DAOFactory daoFactory = new DAOFactory();
        UserDAO userDAO = (UserDAO)daoFactory.getUserDAO();
        FollowDAO followDAO = (FollowDAO)daoFactory.getFollowDAO();

        List<String> followers = new ArrayList<>();
        List<UserDyanmoDBBean> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String alias = "@guy" + i;

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            UserDyanmoDBBean user = new UserDyanmoDBBean();
            user.setAlias(alias);
            user.setFirstName("Guy");
            user.setLastName(Integer.toString(i));
            user.setHashedPassword(hashPassword("password"));
            user.setImageURL("https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
            user.setFollowingCount(1);
            user.setFollowersCount(0);
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(alias);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            userDAO.addUserBatch(users);
        }
        if (followers.size() > 0) {
            followDAO.addFollowersBatch(followers, FOLLOW_TARGET);
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }
}
