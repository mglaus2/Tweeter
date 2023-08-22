package edu.byu.cs.tweeter.server.dao.dynamoDB;

import java.util.HashMap;
import java.util.Map;

import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.FollowDyanmoDBBean;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.StoryDyanmoDBBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StoryDAO implements StoryDAOInterface {
    private static DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<StoryDyanmoDBBean> storyTable;

    private static final String TableName = "story";
    private static final String AuthorAttr = "author_alias";
    private static final String TimestampAttr = "timestamp";

    public StoryDAO() {
        if(enhancedClient == null) {
            DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build();
            enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        }
        storyTable = enhancedClient.table(TableName, TableSchema.fromBean(StoryDyanmoDBBean.class));
    }

    @Override
    public void createPost(String authorAlias, long timestamp, String post) {
        StoryDyanmoDBBean storyDyanmoDBBean = new StoryDyanmoDBBean(authorAlias, timestamp, post);
        storyTable.putItem(storyDyanmoDBBean);
    }

    @Override
    public DataPage<StoryDyanmoDBBean> getStory(String userAlias, int pageSize, Long lastTimeStamp) {
        Key key = Key.builder().partitionValue(userAlias).build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(key))
            .limit(pageSize);

        if(lastTimeStamp != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(AuthorAttr, AttributeValue.builder().s(userAlias).build());
            startKey.put(TimestampAttr, AttributeValue.builder().n(String.valueOf(lastTimeStamp)).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<StoryDyanmoDBBean> result = new DataPage<>();

        PageIterable<StoryDyanmoDBBean> pages = storyTable.query(request);
        pages.stream()
            .limit(1)
            .forEach((Page<StoryDyanmoDBBean> page) -> {
                result.setHasMorePages(page.lastEvaluatedKey() != null);
                page.items().forEach(follower -> result.getValues().add(follower));
            });

        return result;
    }
}
