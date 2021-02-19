package fr.ans.psc.pscload.model.object;

import fr.ans.psc.pscload.model.AttributeInterface;

import java.io.Serializable;

public class Attribute implements Serializable, AttributeInterface {

    private String value;

    public Attribute(String value){
        this.value = value;
    }

    public Attribute() {
    }

    @Override
    public void create() {
        System.out.println("creating attribute with value : " + value);
    }
}
