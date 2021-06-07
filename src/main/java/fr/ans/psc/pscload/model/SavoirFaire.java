package fr.ans.psc.pscload.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class SavoirFaire implements Serializable {

    @JsonProperty("typeCode")
    private String typeCode;

    @JsonProperty("code")
    private String code;

    public SavoirFaire() {}

    public SavoirFaire(String[] items){
        this.typeCode = items[18];
        this.code = items[19];
    }

    public String getExpertiseId() {
        String key = Objects.toString(typeCode, "") +
                Objects.toString(code, "");
        if ("".equals(key)) {
            return "ND";
        }
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavoirFaire)) return false;
        SavoirFaire that = (SavoirFaire) o;
        return Objects.equals(typeCode, that.typeCode) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeCode, code);
    }

    @Override
    public String toString() {
        return typeCode + '|' + code;
    }
}
