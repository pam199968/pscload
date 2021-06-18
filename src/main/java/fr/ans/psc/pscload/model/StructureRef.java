package fr.ans.psc.pscload.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StructureRef {

    @JsonProperty("structureId")
    private String structureId;

    public  StructureRef() {}

    public StructureRef(String structureId) {
        this.structureId = structureId;
    }

    public String getStructureId() {
        return structureId;
    }

}
