package edu.byu.cs.tweeter.server.dao.dynamoDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.net.request.UpdateFeedMessage;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.FeedDyanmoDBBean;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.UserDyanmoDBBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class FeedDAO implements FeedDAOInterface {
    private static DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<FeedDyanmoDBBean> feedTable;

    private static final String TableName = "feed";
    private static final String AliasAttr = "alias";
    private static final String TimestampAttr = "timestamp";

    public FeedDAO() {
        if(enhancedClient == null) {
            DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build();
            enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        }

        feedTable = enhancedClient.table(TableName, TableSchema.fromBean(FeedDyanmoDBBean.class));
    }


    @Override
    public void createFeedItem(String alias, long timestamp, String authorAlias, String post) {
        FeedDyanmoDBBean feedDyanmoDBBean = new FeedDyanmoDBBean(alias, timestamp, authorAlias, post);
        feedTable.putItem(feedDyanmoDBBean);
    }

    @Override
    public DataPage<FeedDyanmoDBBean> getFeed(String alias, int pageSize, Long lastTimeStamp) {
        Key key = Key.builder().partitionValue(alias).build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(key))
            .limit(pageSize);

        if(lastTimeStamp != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(AliasAttr, AttributeValue.builder().s(alias).build());
            startKey.put(TimestampAttr, AttributeValue.builder().n(String.valueOf(lastTimeStamp)).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FeedDyanmoDBBean> result = new DataPage<>();

        PageIterable<FeedDyanmoDBBean> pages = feedTable.query(request);
        pages.stream()
            .limit(1)
            .forEach((Page<FeedDyanmoDBBean> page) -> {
                result.setHasMorePages(page.lastEvaluatedKey() != null);
                page.items().forEach(post -> result.getValues().add(post));
            });

        return result;
    }

    @Override
    public void createBatchFeeds(UpdateFeedMessage updateFeedMessage) {
        String authorAlias = updateFeedMessage.getAuthorAlias();
        long timestamp = updateFeedMessage.getTimestamp();
        String post = updateFeedMessage.getPost();
        List<String> followerAliases = updateFeedMessage.getFollowerAliases();

        List<FeedDyanmoDBBean> feedDTOs = new ArrayList<>();
        FeedDyanmoDBBean feedDyanmoDBBean;

        for(String alias : followerAliases) {
            feedDyanmoDBBean = new FeedDyanmoDBBean(alias, timestamp, authorAlias, post);
            feedDTOs.add(feedDyanmoDBBean);
        }

        writeChunksOfFeedDTOs(feedDTOs);
    }

    private void writeChunksOfFeedDTOs(List<FeedDyanmoDBBean> feedDTOs) {
        DynamoDbTable<FeedDyanmoDBBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedDyanmoDBBean.class));
        WriteBatch.Builder<FeedDyanmoDBBean> writeBuilder = WriteBatch.builder(FeedDyanmoDBBean.class).mappedTableResource(table);
        for (FeedDyanmoDBBean item : feedDTOs) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
            .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunksOfFeedDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
