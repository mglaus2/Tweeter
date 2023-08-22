package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.FollowDyanmoDBBean;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DataPage;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service {

    public FollowService(DAOFactoryInterface daoFactory) {
        super(daoFactory);
    }

    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new FollowingResponse("Authtoken is expired");
        }

        DataPage<FollowDyanmoDBBean> pageOfFollowees = daoFactory.getFollowDAO().getPageOfFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
        String followeeAlias;
        User user;
        List<User> listOfFollowees = new ArrayList<>();
        for(FollowDyanmoDBBean followDyanmoDBBean : pageOfFollowees.getValues()) {
            followeeAlias = followDyanmoDBBean.getFollowee_handle();
            user = daoFactory.getUserDAO().getUser(followeeAlias);
            if(user == null) {
                return new FollowingResponse("[Server Error] Could not find followee by alias");
            }
            listOfFollowees.add(user);
        }

        return new FollowingResponse(listOfFollowees, pageOfFollowees.isHasMorePages());
    }

    public FollowResponse follow(FollowRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new FollowResponse("Authtoken is expired");
        }

        String currUserAlias = daoFactory.getAuthtokenDAO().getCurrUserAlias(request.getAuthToken().getToken());
        String followeeAlias = request.getFollowee().getAlias();

        boolean isFollower = daoFactory.getFollowDAO().isFollower(currUserAlias, followeeAlias);
        if(isFollower) {
            return new FollowResponse("You are already following " + followeeAlias);
        }

        daoFactory.getFollowDAO().createRelationship(currUserAlias, followeeAlias);

        int currUserFollowingCount = daoFactory.getUserDAO().getFollowingCount(currUserAlias);
        daoFactory.getUserDAO().updateFollowingCount(currUserAlias, currUserFollowingCount + 1);

        int followeesFollowersCount = daoFactory.getUserDAO().getFollowersCount(followeeAlias);
        daoFactory.getUserDAO().updateFollowersCount(followeeAlias, followeesFollowersCount + 1);

        return new FollowResponse();
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        } else if(request.getFollower() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new IsFollowerResponse("Authtoken is expired");
        }

        boolean isFollower = daoFactory.getFollowDAO().isFollower(request.getFollower().getAlias(), request.getFollowee().getAlias());
        return new IsFollowerResponse(isFollower);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new FollowersResponse("Authtoken is expired");
        }

        DataPage<FollowDyanmoDBBean> pageOfFollowers = daoFactory.getFollowDAO().getPageOfFollowers(request.getTargetUserAlias(), request.getLimit(), request.getLastFollowerAlias());
        String followerAlias;
        User user;
        List<User> listOfFollowers = new ArrayList<>();
        for(FollowDyanmoDBBean followDyanmoDBBean : pageOfFollowers.getValues()) {
            followerAlias = followDyanmoDBBean.getFollower_handle();
            user = daoFactory.getUserDAO().getUser(followerAlias);
            if(user == null) {
                return new FollowersResponse("[Server Error] Could not find follower by alias");
            }
            listOfFollowers.add(user);
        }

        return new FollowersResponse(listOfFollowers, pageOfFollowers.isHasMorePages());
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new UnfollowResponse("Authtoken is expired");
        }

        String currUserAlias = daoFactory.getAuthtokenDAO().getCurrUserAlias(request.getAuthToken().getToken());
        String followeeAlias = request.getFollowee().getAlias();

        boolean isFollower = daoFactory.getFollowDAO().isFollower(currUserAlias, followeeAlias);
        if(!isFollower) {
            return new UnfollowResponse("You are are not following " + followeeAlias);
        }

        daoFactory.getFollowDAO().removeRelationship(currUserAlias, request.getFollowee().getAlias());

        int currUserFollowingCount = daoFactory.getUserDAO().getFollowingCount(currUserAlias);
        daoFactory.getUserDAO().updateFollowingCount(currUserAlias, currUserFollowingCount - 1);

        int followeesFollowersCount = daoFactory.getUserDAO().getFollowersCount(followeeAlias);
        daoFactory.getUserDAO().updateFollowersCount(followeeAlias, followeesFollowersCount - 1);

        return new UnfollowResponse();
    }

    public FollowingCountResponse getFolloweesCount(FollowingCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new FollowingCountResponse("Authtoken is expired");
        }

        int followeeCount = daoFactory.getUserDAO().getFollowingCount(request.getTargetUser().getAlias());
        if(followeeCount == -1) {
            return new FollowingCountResponse("[Server Error] Unable to get following count from database");
        }

        return new FollowingCountResponse(followeeCount);
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new FollowersCountResponse("Authtoken is expired");
        }

        int followerCount = daoFactory.getUserDAO().getFollowersCount(request.getTargetUser().getAlias());
        if(followerCount == -1) {
            return new FollowersCountResponse("[Server Error] Unable to get followers count from database");
        }

        return new FollowersCountResponse(followerCount);
    }
}
