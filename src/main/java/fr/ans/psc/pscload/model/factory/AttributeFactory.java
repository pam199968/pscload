package fr.ans.psc.pscload.model.factory;

import fr.ans.psc.pscload.model.AttributeInterface;
import fr.ans.psc.pscload.model.object.Attribute;

import java.util.HashMap;

public class AttributeFactory {

    private static final HashMap<String, Attribute> attributeMap = new HashMap<>();

    private AttributeFactory() {}

    public static AttributeInterface getAttribute(String value) {
        return attributeMap.computeIfAbsent(value, k -> new Attribute(value));
    }

}
