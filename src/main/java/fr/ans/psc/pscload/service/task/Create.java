package fr.ans.psc.pscload.service.task;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * The type Create.
 */
public class Create extends Task {

    private final String url;

    private final String json;

    /**
     * Instantiates a new Create.
     *
     * @param url  the url
     * @param json the json
     */
    public Create(String url, String json) {
        this.url = url;
        this.json = json;
    }

    @Override
    public Object call() {
        post(url, json);
        return null;
    }

    /**
     * Post.
     *
     * @param url  the url
     * @param json json request
     */
    private void post(String url, String json) {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = requestBuilder
                .url(url)
                .post(body)
                .build();
        sendRequest(request);
    }

}
