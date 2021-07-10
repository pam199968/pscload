package fr.ans.psc.pscload.service;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class RestUtils {

    private static final Logger log = LoggerFactory.getLogger(RestUtils.class);

    private static final Request.Builder requestBuilder = new Request.Builder();

    private static final OkHttpClient client = new OkHttpClient();

    private RestUtils() {}

    /**
     * Put.
     *
     * @param url  the url
     * @param json json request
     */
    public static void put(String url, String json) {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = requestBuilder
                .url(url)
                .put(body)
                .build();
        sendRequest(request);
    }

    /**
     * Post.
     *
     * @param url  the url
     * @param json json request
     */
    public static void post(String url, String json) {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = requestBuilder
                .url(url)
                .post(body)
                .build();
        sendRequest(request);
    }

    /**
     * Delete.
     *
     * @param url the url
     */
    public static void delete(String url) {
        Request request = requestBuilder
                .url(url)
                .delete()
                .build();
        sendRequest(request);
    }

    private static void sendRequest(Request request) {
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
