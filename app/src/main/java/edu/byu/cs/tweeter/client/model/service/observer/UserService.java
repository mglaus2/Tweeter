package edu.byu.cs.tweeter.client.model.service.observer;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LoginHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LogoutHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.RegisterHandler;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;

public class UserService {
    private ExecuteService executeService;

    public UserService() {
        executeService = new ExecuteService();
    }

    public interface Observer extends BaseObserver<User> {}

    public void getUser(String userAlias, Observer observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                userAlias, new GetUserHandler(observer));
        executeService.executeTask(getUserTask);
    }

    public void loginUser(String alias, String password, Observer observer) throws IOException, TweeterRemoteException {
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(observer));
        executeService.executeTask(loginTask);
    }

    public void registerUser(String firstName, String lastName, String alias, String password, String imageBytesBase64, Observer observer) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password,
                imageBytesBase64, new RegisterHandler(observer));
        executeService.executeTask(registerTask);
    }

    public void logoutUser(Observer observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutHandler(observer));
        executeService.executeTask(logoutTask);
    }
}
