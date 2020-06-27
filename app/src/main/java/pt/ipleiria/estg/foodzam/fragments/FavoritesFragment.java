package pt.ipleiria.estg.foodzam.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ipleiria.estg.foodzam.R;
import pt.ipleiria.estg.foodzam.RecipeAdapter;
import pt.ipleiria.estg.foodzam.RecipeItem;

public class FavoritesFragment extends Fragment {

    private ArrayList<RecipeItem> recipeItems = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View fragment_view = inflater.inflate(R.layout.fragment_favorites, container, false);

        RecyclerView recyclerView = fragment_view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new RecipeAdapter(recipeItems, getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recipeItems.add(new RecipeItem(R.drawable.a, "Photo 01", "0", "0"));
        recipeItems.add(new RecipeItem(R.drawable.b, "Photo 02", "1", "0"));
        recipeItems.add(new RecipeItem(R.drawable.c, "Photo 03", "2", "0"));
        recipeItems.add(new RecipeItem(R.drawable.d, "Photo 04", "3", "0"));

        return fragment_view;
    }
}