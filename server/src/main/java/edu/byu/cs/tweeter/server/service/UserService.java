package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DAOFactoryInterface;

public class UserService extends Service {

    public UserService(DAOFactoryInterface daoFactory) {
        super(daoFactory);
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        User user = daoFactory.getUserDAO().getUser(request.getUsername());
        if(user == null) {
            return new LoginResponse("No users exists with username " + request.getUsername());
        }

        String usersHashedPassword = daoFactory.getUserDAO().getHashedPassword(user.getAlias());
        String hashedPassword = hashPassword(request.getPassword());
        if(!(Objects.equals(usersHashedPassword, hashedPassword))) {
            return new LoginResponse("Password does not associate with " + request.getUsername());
        }

        AuthToken authToken = daoFactory.getAuthtokenDAO().createAuthtoken(user.getAlias());
        return new LoginResponse(user, authToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        } else if(request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        } else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        } else if(request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing a image");
        }

        User user = daoFactory.getUserDAO().getUser(request.getUsername());
        if(user != null) {
            return new RegisterResponse("Users already exists with username " + request.getUsername());
        }

        String hashedPassword = hashPassword(request.getPassword());
        User newUser = daoFactory.getUserDAO().registerUser(request.getFirstName(), request.getLastName(),
            request.getUsername(), hashedPassword, request.getImage());
        if(newUser == null) {
            return new RegisterResponse("Failed to register new user");
        }

        AuthToken authToken = daoFactory.getAuthtokenDAO().createAuthtoken(request.getUsername());
        return new RegisterResponse(newUser, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        daoFactory.getAuthtokenDAO().deleteAuthtoken(request.getAuthToken().getToken());
        return new LogoutResponse();
    }

    public UserResponse getUser(UserRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing a authtoken");
        } else if(request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing the user's alias");
        }

        boolean isValidAuthtoken = validateAuthtoken(request.getAuthToken().getToken());
        if(!isValidAuthtoken) {
            return new UserResponse("Authtoken is expired");
        }

        User user = daoFactory.getUserDAO().getUser(request.getAlias());
        if(user == null) {
            return new UserResponse("No user exists with alias " + request.getAlias());
        }
        return new UserResponse(user);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }
}
