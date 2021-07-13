package fr.ans.psc.pscload.service.task;

import okhttp3.Request;

/**
 * The type Delete.
 */
public class Delete extends Task {

    private final String url;

    /**
     * Instantiates a new Delete.
     *
     * @param url the url
     */
    public Delete(String url) {
        this.url = url;
    }

    @Override
    public void send() {
        delete(url);
    }

    /**
     * Delete.
     *
     * @param url the url
     */
    private void delete(String url) {
        Request request = requestBuilder
                .url(url)
                .delete()
                .build();
        sendRequest(request);
    }

}
