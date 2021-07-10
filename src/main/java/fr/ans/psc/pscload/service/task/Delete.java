package fr.ans.psc.pscload.service.task;

import fr.ans.psc.pscload.service.RestUtils;

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
    public Object call() throws Exception {
        RestUtils.delete(url);
        return null;
    }

}
