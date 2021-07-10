package fr.ans.psc.pscload.service.task;

import fr.ans.psc.pscload.service.RestUtils;

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
    public Object call() throws Exception {
        RestUtils.put(url, json);
        return null;
    }

}
