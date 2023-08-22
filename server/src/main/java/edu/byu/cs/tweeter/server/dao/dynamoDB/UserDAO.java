package edu.byu.cs.tweeter.server.dao.dynamoDB;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.UserDyanmoDBBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class UserDAO implements UserDAOInterface {
    private static DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<UserDyanmoDBBean> userTable;

    private static final String TableName = "user";

    public UserDAO() {
        if(enhancedClient == null) {
            DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build();
            enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        }
        userTable = enhancedClient.table(TableName, TableSchema.fromBean(UserDyanmoDBBean.class));
    }


    @Override
    public User registerUser(String firstName, String lastName, String alias, String hashedPassword, String imageURL) {
        AmazonS3 s3 = AmazonS3ClientBuilder
            .standard()
            .withRegion("us-east-1")
            .build();

        byte[] byteArray = Base64.getDecoder().decode(imageURL);

        ObjectMetadata data = new ObjectMetadata();

        data.setContentLength(byteArray.length);

        data.setContentType("image/jpeg");

        PutObjectRequest request = new PutObjectRequest("mglaus2-tweeter-bucket", alias, new ByteArrayInputStream(byteArray), data).withCannedAcl(CannedAccessControlList.PublicRead);

        s3.putObject(request);

        String link = "https://mglaus2-tweeter-bucket.s3.us-east-1.amazonaws.com/" + alias;
        UserDyanmoDBBean userDyanmoDBBean = new UserDyanmoDBBean(alias, firstName, lastName, hashedPassword, link, 0, 0);
        userTable.putItem(userDyanmoDBBean);
        return new User(firstName, lastName, alias, imageURL);
    }

    @Override
    public User getUser(String alias) {
        Key key = Key.builder().partitionValue(alias).build();
        UserDyanmoDBBean userDyanmoDBBean = userTable.getItem(key);
        if(userDyanmoDBBean == null) {
            return null;
        }

        User user = new User(userDyanmoDBBean.getFirstName(), userDyanmoDBBean.getLastName(),
            userDyanmoDBBean.getAlias(), userDyanmoDBBean.getImageURL());
        return user;
    }

    @Override
    public String getHashedPassword(String alias) {
        Key key = Key.builder().partitionValue(alias).build();
        UserDyanmoDBBean userDyanmoDBBean = userTable.getItem(key);
        if(userDyanmoDBBean == null) {
            return null;
        }
        return userDyanmoDBBean.getHashedPassword();
    }

    @Override
    public int getFollowingCount(String alias) {
        Key key = Key.builder().partitionValue(alias).build();
        UserDyanmoDBBean userDyanmoDBBean = userTable.getItem(key);
        if(userDyanmoDBBean == null) {
            return -1;
        }
        return userDyanmoDBBean.getFollowingCount();
    }

    @Override
    public boolean updateFollowingCount(String alias, int followingCount) {
        Key key = Key.builder().partitionValue(alias).build();
        UserDyanmoDBBean userDyanmoDBBean = userTable.getItem(key);
        userDyanmoDBBean.setFollowingCount(followingCount);
        userTable.updateItem(userDyanmoDBBean);
        return true;
    }

    @Override
    public int getFollowersCount(String alias) {
        Key key = Key.builder().partitionValue(alias).build();
        UserDyanmoDBBean userDyanmoDBBean = userTable.getItem(key);
        if(userDyanmoDBBean == null) {
            return -1;
        }
        return userDyanmoDBBean.getFollowersCount();
    }

    @Override
    public boolean updateFollowersCount(String alias, int followersCount) {
        Key key = Key.builder().partitionValue(alias).build();
        UserDyanmoDBBean userDyanmoDBBean = userTable.getItem(key);
        userDyanmoDBBean.setFollowersCount(followersCount);
        userTable.updateItem(userDyanmoDBBean);
        return true;
    }

    public void addUserBatch(List<UserDyanmoDBBean> users) {
        List<UserDyanmoDBBean> batchToWrite = new ArrayList<>();
        for (UserDyanmoDBBean u : users) {
            batchToWrite.add(u);

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
    private void writeChunkOfUserDTOs(List<UserDyanmoDBBean> userDTOs) {
        if (userDTOs.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<UserDyanmoDBBean> table = enhancedClient.table(TableName, TableSchema.fromBean(UserDyanmoDBBean.class));
        WriteBatch.Builder<UserDyanmoDBBean> writeBuilder = WriteBatch.builder(UserDyanmoDBBean.class).mappedTableResource(table);
        for (UserDyanmoDBBean item : userDTOs) {
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
