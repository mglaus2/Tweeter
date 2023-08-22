package edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class StoryDyanmoDBBean {
    private String author_alias;
    private long timestamp;
    private String post;

    public StoryDyanmoDBBean() {}

    public StoryDyanmoDBBean(String author_alias, Long timestamp, String post) {
        this.author_alias = author_alias;
        this.timestamp = timestamp;
        this.post = post;
    }

    @DynamoDbPartitionKey
    public String getAuthor_alias() {
        return author_alias;
    }

    public void setAuthor_alias(String author_alias) {
        this.author_alias = author_alias;
    }

    @DynamoDbSortKey
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }
}
