package fr.ans.psc.pscload.service.task;

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
    public Object call() {
        restUtils.delete(url);
        return null;
    }

}
