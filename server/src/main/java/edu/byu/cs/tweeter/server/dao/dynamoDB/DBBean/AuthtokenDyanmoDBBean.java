package edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class AuthtokenDyanmoDBBean {
    private String auth_token;
    private String alias;
    private long expirationTime;

    public AuthtokenDyanmoDBBean() {}

    public AuthtokenDyanmoDBBean(String auth_token, String alias, long expirationTime) {
        this.auth_token = auth_token;
        this.alias = alias;
        this.expirationTime = expirationTime;
    }

    @DynamoDbPartitionKey
    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
