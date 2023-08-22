package edu.byu.cs.tweeter.server.dao.dynamoDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.FollowDyanmoDBBean;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.UserDyanmoDBBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
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

public class FollowDAO implements FollowDAOInterface {
    private static DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<FollowDyanmoDBBean> followTable;

    private static final String TableName = "follows";
    public static final String IndexName = "follows_index";

    private static final String FollowerAttr = "follower_handle";
    private static final String FolloweeAttr = "followee_handle";

    FollowDAO() {
        if(enhancedClient == null) {
            DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build();
            enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        }
        followTable = enhancedClient.table(TableName, TableSchema.fromBean(FollowDyanmoDBBean.class));
    }

    @Override
    public void createRelationship(String follower_handle, String followee_handle) {
        FollowDyanmoDBBean followDyanmoDBBean = new FollowDyanmoDBBean(follower_handle, followee_handle);
        followTable.putItem(followDyanmoDBBean);
    }

    @Override
    public void removeRelationship(String follower_handle, String followee_handle) {
        Key key = Key.builder().partitionValue(follower_handle).sortValue(followee_handle).build();
        followTable.deleteItem(key);
    }

    @Override
    public boolean isFollower(String follower_handle, String followee_handle) {
        Key key = Key.builder().partitionValue(follower_handle).sortValue(followee_handle).build();
        FollowDyanmoDBBean followDyanmoDBBean = followTable.getItem(key);
        if(followDyanmoDBBean == null) {
            return false;
        }
        return true;
    }

    @Override
    public DataPage<FollowDyanmoDBBean> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias) {
        DynamoDbIndex<FollowDyanmoDBBean> index = enhancedClient.table(TableName, TableSchema.fromBean(FollowDyanmoDBBean.class)).index(IndexName);
        Key key = Key.builder().partitionValue(targetUserAlias).build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(key))
            .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FollowerAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FollowDyanmoDBBean> result = new DataPage<>();

        SdkIterable<Page<FollowDyanmoDBBean>> sdkIterable = index.query(request);
        PageIterable<FollowDyanmoDBBean> pages = PageIterable.create(sdkIterable);
        pages.stream()
            .limit(1)
            .forEach((Page<FollowDyanmoDBBean> page) -> {
                result.setHasMorePages(page.lastEvaluatedKey() != null);
                page.items().forEach(followee -> result.getValues().add(followee));
            });

        return result;
    }

    @Override
    public DataPage<FollowDyanmoDBBean> getPageOfFollowees(String targetUserAlias, int pageSize, String lastUserAlias) {
        Key key = Key.builder().partitionValue(targetUserAlias).build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(key))
            .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FolloweeAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FollowDyanmoDBBean> result = new DataPage<>();

        PageIterable<FollowDyanmoDBBean> pages = followTable.query(request);
        pages.stream()
            .limit(1)
            .forEach((Page<FollowDyanmoDBBean> page) -> {
                result.setHasMorePages(page.lastEvaluatedKey() != null);
                page.items().forEach(follower -> result.getValues().add(follower));
            });

        return result;
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public void addFollowersBatch(List<String> followers, String followeeAlias) {
        List<FollowDyanmoDBBean> batchToWrite = new ArrayList<>();
        for (String alias : followers) {
            FollowDyanmoDBBean dto = new FollowDyanmoDBBean(alias, followeeAlias);
            batchToWrite.add(dto);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUserDTOs(batchToWrite);
        }
    }
    private void writeChunkOfUserDTOs(List<FollowDyanmoDBBean> followDTOs) {
        if (followDTOs.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<FollowDyanmoDBBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowDyanmoDBBean.class));
        WriteBatch.Builder<FollowDyanmoDBBean> writeBuilder = WriteBatch.builder(FollowDyanmoDBBean.class).mappedTableResource(table);
        for (FollowDyanmoDBBean item : followDTOs) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
            .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
