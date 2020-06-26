package pt.ipleiria.estg.foodzam.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {

    private int number;
    private String step;

    public int getNumber() {
        return number;
    }
    public String getStep() {
        return step;
    }

    @Override
    public String toString() {
        return "Step{" +
                "number=" + number +
                ", step='" + step + '\'' +
                '}';
    }
}
