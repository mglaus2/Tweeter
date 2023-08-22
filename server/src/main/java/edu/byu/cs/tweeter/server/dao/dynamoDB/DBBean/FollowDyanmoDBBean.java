package edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean;

import edu.byu.cs.tweeter.server.dao.dynamoDB.FollowDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FollowDyanmoDBBean {
    private String follower_handle;
    private String followee_handle;

    public FollowDyanmoDBBean() {}

    public FollowDyanmoDBBean(String follower_handle, String followee_handle) {
        this.follower_handle = follower_handle;
        this.followee_handle = followee_handle;
    }

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = FollowDAO.IndexName)
    public String getFollower_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String follower) {
        this.follower_handle = follower;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = FollowDAO.IndexName)
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followee) {
        this.followee_handle = followee;
    }
}
