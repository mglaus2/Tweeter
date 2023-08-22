package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }

    @Override
    protected void runTask() throws IOException, TweeterRemoteException {
        LogoutRequest logoutRequest = new LogoutRequest(getAuthToken());
        LogoutResponse logoutResponse = serverFacade.logout(logoutRequest, "/logout");
        if(!logoutResponse.isSuccess()) {
            sendFailedMessage(logoutResponse.getMessage());
            return;
        }
        sendSuccessMessage();
    }
}
