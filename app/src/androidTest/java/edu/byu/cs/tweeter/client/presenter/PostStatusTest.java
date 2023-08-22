package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class PostStatusTest {
    private GetMainPresenter.View mockMainPresenterView;
    private GetMainPresenter spyMainPresenter;

    private final User TEST_USER = new User("Test", "User", "@testuser", "https://mglaus2-tweeter-bucket.s3.us-east-1.amazonaws.com/@testuser");
    private final Status EXPECTED_TEST_STATUS = new Status("Test Stat", TEST_USER, 0L);

    private CountDownLatch countDownLatch;

    @BeforeEach
    public void setUp() {
        mockMainPresenterView = Mockito.mock(GetMainPresenter.View.class);
        spyMainPresenter = Mockito.spy(new GetMainPresenter(mockMainPresenterView));

        resetCountDownLatch();
    }

    @Test
    public void postStatusTestSuccessful() throws IOException, TweeterRemoteException, ParseException, InterruptedException {
        Answer<Void> answer = invocation -> {
            Thread.sleep(3000);
            return null;
        };

        ServerFacade serverFacade = new ServerFacade();
        LoginRequest loginRequest = new LoginRequest("@testuser", "password");
        LoginResponse loginResponse = serverFacade.login(loginRequest, "/login");
        Cache.getInstance().setCurrUser(loginResponse.getUser());
        Cache.getInstance().setCurrUserAuthToken(loginResponse.getAuthToken());
        Assertions.assertTrue(loginResponse.isSuccess());

        Mockito.doAnswer(answer).when(mockMainPresenterView).displayMessage(Mockito.anyString());
        spyMainPresenter.postStatus("Test Stat");

        //Mockito.verify(mockMainPresenterView).displayMessage(Mockito.anyString());

        StoryRequest storyRequest = new StoryRequest(loginResponse.getAuthToken(), loginRequest.getUsername(), 20, null);
        StoryResponse storyResponse = serverFacade.getStory(storyRequest, "/getstory");
        Assertions.assertTrue(storyResponse.isSuccess());
        Status currStatus = storyResponse.getStatuses().get(0);
        Assertions.assertEquals(EXPECTED_TEST_STATUS.getPost(), currStatus.getPost());
        Assertions.assertEquals(EXPECTED_TEST_STATUS.getUser(), currStatus.getUser());
        Assertions.assertEquals(EXPECTED_TEST_STATUS.getMentions(), currStatus.getMentions());
        Assertions.assertEquals(EXPECTED_TEST_STATUS.getUrls(), currStatus.getUrls());
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }
}
