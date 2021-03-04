package fr.ans.psc.pscload.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class SavoirFaire implements Serializable {

    @JsonProperty("categoryCode")
    private String categoryCode;

    @JsonProperty("code")
    private String code;

    public SavoirFaire() {}

    public SavoirFaire(String[] items){
        this.categoryCode = items[18];
        this.code = items[19];
    }

    public String getKey() {
        return Objects.toString(code, "") +
                Objects.toString(categoryCode, "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavoirFaire)) return false;
        SavoirFaire that = (SavoirFaire) o;
        return Objects.equals(categoryCode, that.categoryCode) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryCode, code);
    }

    @Override
    public String toString() {
        return categoryCode + '|' + code;
    }
}
