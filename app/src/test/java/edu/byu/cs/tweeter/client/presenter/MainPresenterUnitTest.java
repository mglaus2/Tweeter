package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.observer.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;

public class MainPresenterUnitTest {
    private GetMainPresenter.View mockView;
    private StatusService mockStatusService;

    private GetMainPresenter mainPresenterSpy;

    @BeforeEach
    public void setUp() {
        // Create Mocks
        mockView = Mockito.mock(GetMainPresenter.View.class);
        mockStatusService = Mockito.mock(StatusService.class);

        mainPresenterSpy = Mockito.spy(new GetMainPresenter(mockView));
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);
    }

    @Test
    public void testPostStatus_postSuccessful() throws ParseException {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(1, StatusService.PostStatusObserver.class);
                observer.postStatus();
                return null;
            }
        };

        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus("test post");
        Mockito.verify(mockView).postStatus();
    }

    @Test
    public void testPostStatus_postFailedWithMessage() throws ParseException {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(1, StatusService.PostStatusObserver.class);
                observer.handleFailure("the error message");
                return null;
            }
        };

        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus("test post");
        Mockito.verify(mockView, Mockito.times(0)).postStatus();
        Mockito.verify(mockView).displayMessage("Failed to post status: the error message");
    }

    @Test
    public void testPostStatus_postFailedWithException() throws ParseException {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(1, StatusService.PostStatusObserver.class);
                observer.handleException(new Exception("the exception message"));
                return null;
            }
        };

        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus("test post");
        Mockito.verify(mockView, Mockito.times(0)).postStatus();
        Mockito.verify(mockView).displayMessage("Failed to post status because of exception: the exception message");
    }

    @Test
    public void testPostStatus_postStatusParameters() throws ParseException {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Status status = invocation.getArgument(0, Status.class);
                StatusService.PostStatusObserver observer = invocation.getArgument(1, StatusService.PostStatusObserver.class);

                Assertions.assertNotNull(status);
                Assertions.assertEquals("This is a test post @Matthew https://byu.edu", status.getPost());
                Assertions.assertEquals(mainPresenterSpy.getFormattedDateTime(), status.getFormattedDate());

                List<String> mentions = new ArrayList<>();
                List<String> urls = new ArrayList<>();
                mentions.add("@Matthew");
                urls.add("https://byu.edu");

                Assertions.assertEquals(mentions, status.getMentions());
                Assertions.assertEquals(urls, status.getUrls());

                Assertions.assertNotNull(observer);

                return null;
            }
        };

        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus("This is a test post @Matthew https://byu.edu");
    }
}
