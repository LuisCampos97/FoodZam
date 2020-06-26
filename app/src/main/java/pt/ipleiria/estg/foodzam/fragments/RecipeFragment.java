package pt.ipleiria.estg.foodzam.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import pt.ipleiria.estg.foodzam.R;
import pt.ipleiria.estg.foodzam.RecipeDetailsActivity;
import pt.ipleiria.estg.foodzam.helpers.RecipeListAdapter;
import pt.ipleiria.estg.foodzam.helpers.SpoonacularAPI;
import pt.ipleiria.estg.foodzam.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeFragment extends Fragment implements View.OnClickListener{

    private EditText editTextSearch;
    private Button buttonSearch;
    private ListView recipesListView;

    Retrofit retrofit;
    SpoonacularAPI spoonacularAPI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_recipe, container, false);

        editTextSearch = fragment_view.findViewById(R.id.editTextSearch);
        buttonSearch = fragment_view.findViewById(R.id.buttonSearch);
        recipesListView = fragment_view.findViewById(R.id.recipesListView);

        buttonSearch.setOnClickListener(this);

        retrofit = new Retrofit.Builder().baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        spoonacularAPI = retrofit.create(SpoonacularAPI.class);

        /*if (getArguments().get("ingredient") != null) {
            String ingredient = getArguments().getString("ingredient");
            editTextSearch.setText(ingredient);
            buttonSearch.performClick();
        } */

        return fragment_view;
    }

    public void apiCall(String food) {
        Call<List<Recipe>> listCall = spoonacularAPI.getRecipesByIngredients(food, 5, getString(R.string.spoonacular_api_key));

        listCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Code: " + response.code());
                    return;
                }

                List<Recipe> recipes = response.body();

                RecipeListAdapter adapter = new RecipeListAdapter(requireActivity(), R.layout.row, recipes);
                recipesListView.setAdapter(adapter);

                recipesListView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
                    intent.putExtra("recipeId", recipes.get(position).getId());
                    startActivity(intent);
                });
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSearch) {
            String ingredient = editTextSearch.getText().toString();
            apiCall(ingredient);
        }
    }
}