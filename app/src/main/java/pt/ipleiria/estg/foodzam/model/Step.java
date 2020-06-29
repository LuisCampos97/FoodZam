package pt.ipleiria.estg.foodzam.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {

    private int number;
    private String step;

    public Step(int number, String step) {
        this.number = number;
        this.step = step;
    }

    public Step() {
    }

    public int getNumber() {
        return number;
    }
    public String getStep() {
        return step;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "Step{" +
                "number=" + number +
                ", step='" + step + '\'' +
                '}';
    }
}
