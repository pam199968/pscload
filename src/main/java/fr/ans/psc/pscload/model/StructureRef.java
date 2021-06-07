package fr.ans.psc.pscload.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StructureRef {

    @JsonProperty("structureId")
    private final String structureId;

    public StructureRef(String structureId) {
        this.structureId = structureId;
    }

}
