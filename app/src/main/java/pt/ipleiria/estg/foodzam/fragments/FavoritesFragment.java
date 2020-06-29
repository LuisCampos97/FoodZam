package pt.ipleiria.estg.foodzam.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import pt.ipleiria.estg.foodzam.R;
import pt.ipleiria.estg.foodzam.RecipeDetailsActivity;
import pt.ipleiria.estg.foodzam.helpers.FavoritesRecipesAdapter;
import pt.ipleiria.estg.foodzam.helpers.SpoonacularAPI;
import pt.ipleiria.estg.foodzam.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoritesFragment extends Fragment {

    Retrofit retrofit;
    SpoonacularAPI spoonacularAPI;
    RecyclerView recyclerView;
    private FirebaseFirestore db;
    private String ids;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View fragment_view = inflater.inflate(R.layout.fragment_favorites, container, false);

        retrofit = new Retrofit.Builder().baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        spoonacularAPI = retrofit.create(SpoonacularAPI.class);

        recyclerView = fragment_view.findViewById(R.id.recyclerView);

        ids = "";

        db = FirebaseFirestore.getInstance();
        db.collection("favorites").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ids += document.getData().get("id") + ",";
                }

                apiCall(ids);

            }
        });

        return fragment_view;
    }

    public void apiCall(String ids) {
        Call<List<Recipe>> listCall = spoonacularAPI.getRecipeInformationByIds(ids, getString(R.string.spoonacular_api_key));

        listCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Code: " + response.code());
                    return;
                }

                List<Recipe> recipes = response.body();

                recyclerView.setHasFixedSize(true);
                FavoritesRecipesAdapter adapter = new FavoritesRecipesAdapter(recipes, getActivity());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter.notifyDataSetChanged();

                adapter.setClickListener(v -> {
                    int position = recyclerView.indexOfChild(v);
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
}