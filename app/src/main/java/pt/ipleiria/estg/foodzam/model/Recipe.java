package pt.ipleiria.estg.foodzam.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

    private int id;
    private String title;
    private String image;

    @SerializedName("extendedIngredients")
    private List<Ingredient> ingredients;

    private int readyInMinutes;
    private int servings;
    private List<AnalyzedInstructions> analyzedInstructions;

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

    public List<Ingredient> getIngredients() {
        return ingredients;
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

    // Inner Classes
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Step {
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnalyzedInstructions {

        private List<Step> steps;

        public List<Step> getSteps() {
            return steps;
        }
    }

    @Override
    public String toString() {
        return title;
    }
}
