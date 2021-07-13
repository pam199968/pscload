package fr.ans.psc.pscload.service.task;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * The type Task.
 */
public abstract class Task implements Callable<Object> {

    private static final Logger log = LoggerFactory.getLogger(Task.class);

    private final OkHttpClient client = new OkHttpClient();

    final Request.Builder requestBuilder = new Request.Builder();

    @Override
    public Object call() {
        return null;
    }

    void sendRequest(Request request) {
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String responseBody = Objects.requireNonNull(response.body()).string();
            log.info("response body: {}", responseBody);
            response.close();
        } catch (IOException e) {
            log.error("error: {}", e.getMessage());
        }
    }

}
