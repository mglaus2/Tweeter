package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusMessage;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.request.UpdateFeedMessage;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.FeedDyanmoDBBean;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.FollowDyanmoDBBean;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DBBean.StoryDyanmoDBBean;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DataPage;

public class StatusService extends Service {

    private final String SQS_POST_STATUS_QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/411458973896/Tweeter_PostStatusQueue";
    private final String SQS_UPDATE_FEED_QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/411458973896/Tweeter_UpdateFeedQueue";

    public StatusService(DAOFactoryInterface daoFactory) {
        super(daoFactory);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if(request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new PostStatusResponse("Authtoken is expired");
        }

        String authorAlias = request.getStatus().getUser().getAlias();
        Long timeStamp = System.currentTimeMillis();
        String post = request.getStatus().getPost();

        daoFactory.getStoryDAO().createPost(authorAlias, timeStamp, post);

        initiatePostStatusQueue(authorAlias, timeStamp, post);

        return new PostStatusResponse();
    }

    private void initiatePostStatusQueue(String authorAlias, Long timestamp, String post) {
        PostStatusMessage postStatusMessage = new PostStatusMessage(authorAlias, timestamp, post);
        String messageBody = new Gson().toJson(postStatusMessage);

        SendMessageRequest send_msg_request = new SendMessageRequest()
            .withQueueUrl(SQS_POST_STATUS_QUEUE_URL)
            .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(send_msg_request);
    }

    public void postStatusFeedMessages(SQSEvent sqsEvent) {
        System.out.println("PostUpdateFeedMessages Initialized");
        if (sqsEvent.getRecords() == null) {
            System.out.println("Empty message sent");
            return;
        }

        PostStatusMessage postStatusMessage;
        DataPage<FollowDyanmoDBBean> followers;
        List<String> followersAliases;

        for(SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
            String bodyMessage = message.getBody();

            postStatusMessage = new Gson().fromJson(bodyMessage, PostStatusMessage.class);
            followers = daoFactory.getFollowDAO().getPageOfFollowers(postStatusMessage.getAuthorAlias(), 1000000, null);
            followersAliases = new ArrayList<>();
            for(FollowDyanmoDBBean followDyanmoDBBean : followers.getValues()) {
                followersAliases.add(followDyanmoDBBean.getFollower_handle());
            }

            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

            List<String> currBatch = new ArrayList<>();
            UpdateFeedMessage updateFeedMessage;
            String messageBody;

            for(String alias : followersAliases) {
                currBatch.add(alias);
                if(currBatch.size() == 25) {
                    updateFeedMessage = new UpdateFeedMessage(postStatusMessage.getAuthorAlias(), postStatusMessage.getTimestamp(), postStatusMessage.getPost(), currBatch);
                    messageBody = new Gson().toJson(updateFeedMessage);

                    SendMessageRequest send_msg_request = new SendMessageRequest()
                        .withQueueUrl(SQS_UPDATE_FEED_QUEUE_URL)
                        .withMessageBody(messageBody);

                    sqs.sendMessage(send_msg_request);
                    System.out.println("Sent messageBody:" + messageBody);

                    currBatch = new ArrayList<>();
                }
            }
            if(currBatch.size() != 0) {
                updateFeedMessage = new UpdateFeedMessage(postStatusMessage.getAuthorAlias(), postStatusMessage.getTimestamp(), postStatusMessage.getPost(), currBatch);
                messageBody = new Gson().toJson(updateFeedMessage);

                SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(SQS_UPDATE_FEED_QUEUE_URL)
                    .withMessageBody(messageBody);

                sqs.sendMessage(send_msg_request);
                System.out.println("Sent messageBody:" + messageBody);
            }
        }
    }

    public void updateFeeds(SQSEvent sqsEvent) {
        System.out.println("UpdateFeeds was Initialized");
        if (sqsEvent.getRecords() == null) {
            System.out.println("Empty message sent");
            return;
        }

        UpdateFeedMessage updateFeedMessage;
        String messageBody;

        for(SQSEvent.SQSMessage sqsMessage : sqsEvent.getRecords()) {
            messageBody = sqsMessage.getBody();
            updateFeedMessage = new Gson().fromJson(messageBody, UpdateFeedMessage.class);
            daoFactory.getFeedDAO().createBatchFeeds(updateFeedMessage);
        }
    }

    public StoryResponse getStory(StoryRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new StoryResponse("Authtoken is expired");
        }

        String userAlias = request.getTargetUserAlias();
        Status status = request.getLastStatus();
        Long timestamp;
        if(status != null) {
            timestamp = status.getTimestamp();
        }
        else {
            timestamp = null;
        }

        DataPage<StoryDyanmoDBBean> pageOfStoryStatuses = daoFactory.getStoryDAO().getStory(userAlias, request.getLimit(), timestamp);
        String authorAlias;
        String post;
        User user;
        List<Status> listOfStatuses = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        List<String> mentions = new ArrayList<>();

        for(StoryDyanmoDBBean storyDyanmoDBBean : pageOfStoryStatuses.getValues()) {
            authorAlias = storyDyanmoDBBean.getAuthor_alias();
            user = daoFactory.getUserDAO().getUser(authorAlias);
            if(user == null) {
                return new StoryResponse("[Server Error] Could not find user with alias");
            }

            post = storyDyanmoDBBean.getPost();
            urls = getUrls(post);
            mentions = getMentions(post);
            status = new Status(post, user, storyDyanmoDBBean.getTimestamp(), urls, mentions);
            listOfStatuses.add(status);
        }

        return new StoryResponse(listOfStatuses, pageOfStoryStatuses.isHasMorePages());
    }

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new FeedResponse("Authtoken is expired");
        }

        String alias = request.getTargetUserAlias();
        Status status = request.getLastStatus();
        Long timestamp;
        if(status != null) {
            timestamp = status.getTimestamp();
        }
        else {
            timestamp = null;
        }

        DataPage<FeedDyanmoDBBean> pageOfFeedStatuses = daoFactory.getFeedDAO().getFeed(alias, request.getLimit(), timestamp);
        String authorAlias;
        User user;
        String post;
        List<Status> listOfStatuses = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        List<String> mentions = new ArrayList<>();

        for(FeedDyanmoDBBean feedDyanmoDBBean : pageOfFeedStatuses.getValues()) {
            authorAlias = feedDyanmoDBBean.getAuthorAlias();
            user = daoFactory.getUserDAO().getUser(authorAlias);
            if(user == null) {
                return new FeedResponse("[Server Error] Could not find user with alias");
            }

            post = feedDyanmoDBBean.getPost();
            urls = getUrls(post);
            mentions = getMentions(post);
            status = new Status(post, user, feedDyanmoDBBean.getTimestamp(), urls, mentions);
            listOfStatuses.add(status);
        }

        return new FeedResponse(listOfStatuses, pageOfFeedStatuses.isHasMorePages());
    }

    private List<String> getMentions(String post) {
        List<String> mentions = new ArrayList<>();
        String[] eachWordOfPost = post.split(" ");
        for(String word : eachWordOfPost) {
            if(word.charAt(0) == '@') {
                mentions.add(word);
            }
        }

        return mentions;
    }

    private List<String> getUrls(String post) {
        List<String> urls = new ArrayList<>();
        String[] eachWordOfPost = post.split(" ");
        for(String word : eachWordOfPost) {
            if(word.contains("https") || word.contains(".com") || word.contains(".net") || word.contains(".org") || word.contains("http")) {
                urls.add(word);
            }
        }

        return urls;
    }
}
