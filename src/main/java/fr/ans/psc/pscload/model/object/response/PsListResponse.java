package fr.ans.psc.pscload.model.object.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import fr.ans.psc.pscload.model.object.Professionnel;

import java.io.Serializable;

public class PsListResponse implements Serializable {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private Professionnel[] data;

}
