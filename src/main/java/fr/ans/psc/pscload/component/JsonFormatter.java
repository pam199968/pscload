package fr.ans.psc.pscload.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.ans.psc.pscload.model.object.Professionnel;
import org.springframework.stereotype.Component;

/**
 * The type Psc rest api.
 */
@Component
public class JsonFormatter {

    private final Gson gson;

    /**
     * Instantiates a new JsonFormatter.
     *
     * @param gson gson
     */
    public JsonFormatter(Gson gson) {
        this.gson = gson;
    }

    /**
     * Instantiates a JsonFormatter.
     */
    public JsonFormatter() {
        GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
        this.gson = builder.create();
    }

    /**
     * Ps from message string.
     *
     * @param message the message
     * @return the string
     */
    public String psFromMessage(String message) {
        String[] items = message.split("\\|", -1);
        Professionnel ps = new Professionnel(items);
        return psFromObject(ps);
    }

    /**
     * Ps from object string.
     *
     * @param ps the ps
     * @return the string
     */
    public String psFromObject(Professionnel ps) {
        return gson.toJson(ps);
    }

    /**
     * Naked ps from object string.
     *
     * @param ps the ps
     * @return the string
     */
    public String nakedPsFromObject(Professionnel ps) {
        return gson.toJson(new Professionnel(ps));
    }

    /**
     * Naked ps from message string.
     *
     * @param message the message
     * @return the string
     */
    public String nakedPsFromMessage(String message) {
        String[] items = message.split("\\|", -1);
        Professionnel ps = new Professionnel(items);
        return gson.toJson(new Professionnel(ps));
    }

}
