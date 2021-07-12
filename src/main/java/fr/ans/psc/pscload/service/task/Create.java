package fr.ans.psc.pscload.service.task;

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
        restUtils.post(url, json);
        return null;
    }

}
