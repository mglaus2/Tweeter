package edu.byu.cs.tweeter.client.model.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.net.TweeterRequestException;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeTest {
    ServerFacade serverFacade = new ServerFacade();
    AuthToken authToken = new AuthToken();

    /**
     * Test user profile images.
     */
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    private static final String FEMALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png";

    /**
     * Generated users.
     */
    private static final User user1 = new User("Allen", "Anderson", "@allen", MALE_IMAGE_URL);
    private static final User user2 = new User("Amy", "Ames", "@amy", FEMALE_IMAGE_URL);
    private static final User user3 = new User("Bob", "Bobson", "@bob", MALE_IMAGE_URL);

    @Test
    public void registerSuccessTest() throws IOException, TweeterRemoteException {
        RegisterRequest registerRequest = new RegisterRequest("test", "test", "@test", "test", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        RegisterResponse registerResponse = Assertions.assertDoesNotThrow(() -> serverFacade.register(registerRequest, "/register"));

        RegisterResponse expectedResponse = new RegisterResponse(user1, authToken);
        Assertions.assertEquals(expectedResponse.getUser(), registerResponse.getUser());
        Assertions.assertNull(registerResponse.getMessage());
        Assertions.assertTrue(registerResponse.isSuccess());
    }

    @Test
    public void registerExceptionThrown() throws IOException, TweeterRemoteException {
        RegisterRequest registerRequest = new RegisterRequest(null, null, null, null, null);
        TweeterRequestException thrown = Assertions.assertThrows(TweeterRequestException.class, () -> serverFacade.register(registerRequest, "/register"));
        Assertions.assertEquals("[Bad Request] Missing a username", thrown.getMessage());
    }

    @Test
    public void getFollowersSuccessTest() {
        FollowersRequest followersRequest = new FollowersRequest(authToken, "@allen", 3, null);
        FollowersResponse followersResponse = Assertions.assertDoesNotThrow(() -> serverFacade.getFollowers(followersRequest, "/getfollowers"));
        FollowersResponse expectedResponse = new FollowersResponse(Arrays.asList(user1, user2, user3), true);

        Assertions.assertEquals(expectedResponse.getFollowers(), followersResponse.getFollowers());
        Assertions.assertEquals(expectedResponse.getHasMorePages(), followersResponse.getHasMorePages());
        Assertions.assertNull(followersResponse.getMessage());
        Assertions.assertTrue(followersResponse.isSuccess());
        Assertions.assertEquals(expectedResponse, followersResponse);
    }

    @Test
    public void getFollowersExceptionThrown() {
        FollowersRequest followersRequest = new FollowersRequest(authToken, "@allen", -1, null);
        TweeterRequestException thrown = assertThrows(TweeterRequestException.class, () -> serverFacade.getFollowers(followersRequest, "/getfollowers"));
        Assertions.assertEquals("[Bad Request] Request needs to have a positive limit", thrown.getMessage());
    }

    @Test
    public void getFollowingCountSuccessTest() {
        FollowingCountRequest followingCountRequest = new FollowingCountRequest(authToken, user2);
        FollowingCountResponse followingCountResponse = Assertions.assertDoesNotThrow(() -> serverFacade.getFollowingCount(followingCountRequest, "/getfollowingcount"));
        FollowingCountResponse expectedResponse = new FollowingCountResponse(getFakeData().getFakeUsers().size());

        Assertions.assertEquals(expectedResponse.getFolloweeCount(), followingCountResponse.getFolloweeCount());
        Assertions.assertNull(followingCountResponse.getMessage());
        Assertions.assertTrue(followingCountResponse.isSuccess());
    }

    @Test
    public void getFollowingCountExceptionsThrown() {
        FollowingCountRequest followingCountRequest = new FollowingCountRequest(null, user2);
        TweeterRequestException thrown = Assertions.assertThrows(TweeterRequestException.class, () -> serverFacade.getFollowingCount(followingCountRequest, "/getfollowingcount"));
        Assertions.assertEquals("[Bad Request] Request needs to have an authtoken", thrown.getMessage());
    }

    private FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
