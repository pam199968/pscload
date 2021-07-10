package fr.ans.psc.pscload.service.task;

import java.util.concurrent.Callable;

/**
 * The type Task.
 */
public abstract class Task implements Callable<Object> {

    @Override
    public Object call() throws Exception {
        return null;
    }

}
