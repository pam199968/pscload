package fr.ans.psc.pscload.component;

import com.google.gson.*;
import fr.ans.psc.pscload.model.object.ExerciceProfessionnel;
import fr.ans.psc.pscload.model.object.Professionnel;
import fr.ans.psc.pscload.model.object.SavoirFaire;
import fr.ans.psc.pscload.model.object.SituationExercice;
import fr.ans.psc.pscload.model.object.response.PsListResponse;
import fr.ans.psc.pscload.model.object.response.PsResponse;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

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
        this.gson = new GsonBuilder().disableHtmlEscaping()
                .registerTypeHierarchyAdapter(List.class, new CollectionAdapter()).create();
    }

    /**
     * The type Collection adapter.
     */
    static class CollectionAdapter implements JsonSerializer<List<?>> {
        @Override
        public JsonElement serialize(List<?> src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null || src.isEmpty()) // exclusion is made here
                return null;

            JsonArray array = new JsonArray();

            for (Object child : src) {
                if ((child instanceof SavoirFaire && ((SavoirFaire) child).getCompositeId().equals("")) ||
                        (child instanceof SituationExercice && ((SituationExercice) child).getCompositeId().equals(""))) {
                    // do nothing
                } else {
                    JsonElement element = context.serialize(child);
                    array.add(element);
                }
            }
            return array;
        }
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
        return jsonFromObject(ps);
    }

    /**
     * Json from object string.
     *
     * @param o the object
     * @return the string
     */
    public String jsonFromObject(Object o) {
        return gson.toJson(o);
    }

    public PsListResponse psListFromJson(String json) {
        return gson.fromJson(json, PsListResponse.class);
    }

    public PsResponse psFromJson(String json) {
        return gson.fromJson(json, PsResponse.class);
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

    /**
     * Naked ex pro from object string.
     *
     * @param exPro the ex pro
     * @return the string
     */
    public String nakedExProFromObject(ExerciceProfessionnel exPro) {
        return gson.toJson(new ExerciceProfessionnel(exPro));
    }
}
