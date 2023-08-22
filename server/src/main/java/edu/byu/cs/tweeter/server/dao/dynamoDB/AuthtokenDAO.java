package edu.byu.cs.tweeter.server.dao.dynamoDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.AuthtokenDyanmoDBBean;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.FeedDyanmoDBBean;
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

public class AuthtokenDAO implements AuthtokenDAOInterface {
    private static DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<AuthtokenDyanmoDBBean> authtokenTable;

    private final long TOKEN_LIFESPAN = 5000000L;
    private static final String TableName = "authtoken";

    public AuthtokenDAO() {
        if(enhancedClient == null) {
            DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build();
            enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        }

        authtokenTable = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenDyanmoDBBean.class));
    }


    @Override
    public AuthToken createAuthtoken(String alias) {
        String token = UUID.randomUUID().toString();
        long currTimestamp = System.currentTimeMillis();
        long expirationTime = calculateExpirationTime(currTimestamp);

        AuthtokenDyanmoDBBean newAuthtokenBean = new AuthtokenDyanmoDBBean(token, alias, expirationTime);
        authtokenTable.putItem(newAuthtokenBean);
        //System.out.println("Authtoken added: " + token + "alais: " + alias + "Expiration time: " + getFormattedDate(expirationTime));
        return new AuthToken(token, getFormattedDate(currTimestamp));
    }

    @Override
    public void deleteAuthtoken(String auth_token) {
        Key key = Key.builder().partitionValue(auth_token).build();
        authtokenTable.deleteItem(key);
    }

    @Override
    public String getCurrUserAlias(String token) {
        Key key = Key.builder().partitionValue(token).build();
        AuthtokenDyanmoDBBean authtokenDyanmoDBBean = authtokenTable.getItem(key);
        return authtokenDyanmoDBBean.getAlias();
    }

    @Override
    public long getAuthtokenExpirationTime(String token) {
        Key key = Key.builder().partitionValue(token).build();
        AuthtokenDyanmoDBBean authtokenDyanmoDBBean = authtokenTable.getItem(key);
        return authtokenDyanmoDBBean.getExpirationTime();
    }

    @Override
    public void updateAuthtokenExpirationTime(String token) {
        Key key = Key.builder().partitionValue(token).build();
        AuthtokenDyanmoDBBean authtokenDyanmoDBBean = authtokenTable.getItem(key);
        authtokenDyanmoDBBean.setExpirationTime(calculateExpirationTime(System.currentTimeMillis()));
        authtokenTable.updateItem(authtokenDyanmoDBBean);
    }

    private long calculateExpirationTime(long currTimestamp) {
        return currTimestamp + TOKEN_LIFESPAN;
    }

    private String getFormattedDate(long timestamp) {
        return new SimpleDateFormat("E MMM d k:mm:ss z y", Locale.US).format(new Date(timestamp));
    }
}
