package pt.ipleiria.estg.foodzam.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyzedInstructions {
    private List<Step> steps;

    public List<Step> getSteps() {
        return steps;
    }
}
