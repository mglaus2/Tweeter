package edu.byu.cs.tweeter.client.model.service.observer;

public interface ServiceObserver<T> {
    void handleFailure(String message);
    void handleException(Exception exception);
}

