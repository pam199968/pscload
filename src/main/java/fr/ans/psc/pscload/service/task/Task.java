package fr.ans.psc.pscload.service.task;

import fr.ans.psc.pscload.service.RestUtils;

import java.util.concurrent.Callable;

/**
 * The type Task.
 */
public abstract class Task implements Callable<Object> {

    final RestUtils restUtils = new RestUtils();

    @Override
    public Object call() {
        return null;
    }

}
