package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.observer.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusServiceTest {

    private User user1;
    private StatusService statusServiceSpy;
    private StatusServiceObserver observer;

    private CountDownLatch countDownLatch;

    /**
     * Test user profile images.
     */
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";

    /**
     * Create a FollowService spy that uses a mock ServerFacade to return known responses to
     * requests.
     */
    @BeforeEach
    public void setup() {
        user1 = new User("Allen", "Anderson", "@allen", MALE_IMAGE_URL);

        statusServiceSpy = Mockito.spy(new StatusService());

        // Setup an observer for the FollowService
        observer = new StatusServiceObserver();

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    private class StatusServiceObserver implements StatusService.Observer {

        private boolean success;
        private String message;
        private List<Status> statuses;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void handleFailure(String message) {
            this.success = false;
            this.message = message;
            this.statuses = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception exception) {
            this.success = false;
            this.message = null;
            this.statuses = null;
            this.hasMorePages = false;
            this.exception = exception;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }

        @Override
        public void addItems(List<Status> statuses, boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.statuses = statuses;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }
    }


    @Test
    public void testGetStory_validRequest_correctResponse() throws InterruptedException {
        statusServiceSpy.loadStory(user1, 3, null, observer);
        awaitCountDownLatch();

        List<Status> expectedStatuses = FakeData.getInstance().getFakeStatuses().subList(0, 3);
        for(Status status : expectedStatuses) {
            status.setTimeStamp(observer.statuses.get(0).getTimestamp());
        }
        Assertions.assertTrue(observer.isSuccess());
        Assertions.assertNull(observer.getMessage());
        Assertions.assertEquals(expectedStatuses, observer.getStatuses());
        Assertions.assertTrue(observer.getHasMorePages());
        Assertions.assertNull(observer.getException());
    }

    @Test
    public void testGetStory_validRequest_loadsStatuses() throws InterruptedException {
        statusServiceSpy.loadStory(user1, 3, null, observer);
        awaitCountDownLatch();

        List<Status> statuses = observer.getStatuses();
        Assertions.assertTrue(statuses.size() > 0);
    }

    @Test
    public void testGetStory_invalidRequest_returnsNoStatuses() throws InterruptedException {
        statusServiceSpy.loadStory(null, 0, null, observer);
        awaitCountDownLatch();

        Assertions.assertFalse(observer.isSuccess());
        Assertions.assertNull(observer.getMessage());
        Assertions.assertNull(observer.getStatuses());
        Assertions.assertFalse(observer.getHasMorePages());
        Assertions.assertNotNull(observer.getException());
    }
}
