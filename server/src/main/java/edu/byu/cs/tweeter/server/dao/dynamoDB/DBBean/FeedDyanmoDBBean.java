package edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FeedDyanmoDBBean {
    private String alias;
    private long timestamp;
    private String authorAlias;
    private String post;

    public FeedDyanmoDBBean() {}

    public FeedDyanmoDBBean(String alias, long timestamp, String authorAlias, String post) {
        this.alias = alias;
        this.timestamp = timestamp;
        this.authorAlias = authorAlias;
        this.post = post;
    }

    @DynamoDbPartitionKey
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @DynamoDbSortKey
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthorAlias() {
        return authorAlias;
    }

    public void setAuthorAlias(String authorAlias) {
        this.authorAlias = authorAlias;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }
}
