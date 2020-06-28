package pt.ipleiria.estg.foodzam.helpers;

import java.util.List;

import pt.ipleiria.estg.foodzam.model.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularAPI {

    @GET("recipes/findByIngredients")
    Call<List<Recipe>> getRecipesByIngredients(@Query("ingredients") String ingredients,
                                               @Query("number") int number,
                                               @Query("apiKey") String apiKey);

    @GET("recipes/{id}/information")
    Call<Recipe> getRecipeInformation(@Path("id") int recipedId,
                                      @Query("apiKey") String apiKey);

    @GET("recipes/informationBulk")
    Call<List<Recipe>> getRecipeInformationByIds(@Query("ids") String ids,
                                      @Query("apiKey") String apiKey);
}
