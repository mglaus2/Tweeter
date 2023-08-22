package edu.byu.cs.tweeter.client.model.service.observer;

public interface BaseObserver<T> extends ServiceObserver {
    void handleSuccess(T data);
}
