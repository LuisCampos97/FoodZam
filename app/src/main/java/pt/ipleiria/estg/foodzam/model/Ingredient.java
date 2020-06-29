package pt.ipleiria.estg.foodzam.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {

    private String name;
    private String original;

    public Ingredient(String original) {
        this.original = original;
    }

    public Ingredient() {
    }

    public String getName() {
        return name;
    }

    public String getOriginal() {
        return original;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    @Override
    public String toString() {
        return original;
    }
}
