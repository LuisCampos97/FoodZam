package pt.ipleiria.estg.foodzam.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

    private int id;
    private String title;
    private String image;
    private int readyInMinutes;
    private int servings;
    private List<AnalyzedInstructions> analyzedInstructions;
    private List<Ingredient> extendedIngredients;

    // Getters Methods
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    public int getServings() {
        return servings;
    }

    public List<AnalyzedInstructions> getAnalyzedInstructions() {
        return analyzedInstructions;
    }

    public List<Ingredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    @Override
    public String toString() {
        return title;
    }
}
