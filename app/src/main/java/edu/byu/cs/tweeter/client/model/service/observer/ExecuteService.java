package edu.byu.cs.tweeter.client.model.service.observer;

import java.util.concurrent.Executors;

public class ExecuteService<T> {
    public void executeTask(T task) {
        java.util.concurrent.ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute((Runnable) task);
    }
}
