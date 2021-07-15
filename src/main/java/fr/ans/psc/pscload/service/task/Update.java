package fr.ans.psc.pscload.service.task;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * The type Update.
 */
public class Update extends Task {

    private final String url;

    private final String json;

    /**
     * Instantiates a new Update.
     *
     * @param url  the url
     * @param json the json
     */
    public Update(String url, String json) {
        this.url = url;
        this.json = json;
    }

    @Override
    public void send() {
        put(url, json);
    }

    /**
     * Put.
     *
     * @param url  the url
     * @param json json request
     */
    private void put(String url, String json) {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = requestBuilder
                .url(url)
                .put(body)
                .build();
        sendRequest(request);
    }

}
