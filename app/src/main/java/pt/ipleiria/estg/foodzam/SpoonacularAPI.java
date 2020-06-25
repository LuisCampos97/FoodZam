package pt.ipleiria.estg.foodzam;

import java.util.List;

import pt.ipleiria.estg.foodzam.model.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SpoonacularAPI {

    @GET("recipes/findByIngredients")
    Call<List<Recipe>> getRecipesByIngredients(@Query("ingredients") String ingredients,
                                               @Query("number") String number,
                                               @Query("apiKey") String apiKey);
}
