package fr.ans.psc.pscload.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SavoirFaire implements Serializable {

    @JsonProperty("categoryCode")
    private String categoryCode;

    @JsonProperty("code")
    private String code;

    public SavoirFaire(String[] items){
        this.categoryCode = items[18];
        this.code = items[19];
    }

}
