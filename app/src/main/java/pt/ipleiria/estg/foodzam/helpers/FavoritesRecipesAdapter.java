package pt.ipleiria.estg.foodzam.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import pt.ipleiria.estg.foodzam.R;
import pt.ipleiria.estg.foodzam.RecipeDetailsActivity;
import pt.ipleiria.estg.foodzam.model.Recipe;

public class FavoritesRecipesAdapter extends RecyclerView.Adapter<FavoritesRecipesAdapter.ViewHolder> implements View.OnClickListener {

    private List<Recipe> recipeItems;
    private Context context;
    private FirebaseFirestore db;

    public FavoritesRecipesAdapter(List<Recipe> recipeItems, Context context){
        this.recipeItems = recipeItems;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_favorites_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesRecipesAdapter.ViewHolder holder, int position) {
        final Recipe recipeItem = recipeItems.get(position);

        Glide.with(context).load(recipeItem.getImage()).into(holder.imageView);
        holder.titleTextView.setText(recipeItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return recipeItems.size();
    }

    @Override
    public void onClick(View v) {
        //TODO: Click no card para ir para os RecipeDetails
        //Intent intent = new Intent(context, RecipeDetailsActivity.class);
        //intent.putExtra("recipeId", recipeItems.get(position).getId());
        //context.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        Button favBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewFavorites);
            titleTextView = itemView.findViewById(R.id.textViewTitleFavorite);
            favBtn = itemView.findViewById(R.id.favBtn);

            favBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Recipe recipe = recipeItems.get(position);

                AtomicBoolean isFavorite = new AtomicBoolean(false);
                AtomicReference<String> documentId = new AtomicReference<>("");

                db.collection("favorites")
                        .whereEqualTo("id", recipe.getId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if((Long) document.getData().get("id") == recipe.getId()) {
                                        favBtn.setBackgroundResource(R.drawable.ic_favorite_red);
                                        isFavorite.set(true);
                                        documentId.set(document.getId());
                                    } else {
                                        favBtn.setBackgroundResource(R.drawable.ic_favorite_shadow);
                                        isFavorite.set(false);
                                    }
                                }

                                if(!isFavorite.get()) {
                                    Map<String, Object> recipeMap = new HashMap();
                                    recipeMap.put("id", recipe.getId());

                                    db.collection("favorites")
                                            .add(recipeMap)
                                            .addOnSuccessListener(documentReference -> {
                                                favBtn.setBackgroundResource(R.drawable.ic_favorite_red);
                                                isFavorite.set(true);
                                            })
                                            .addOnFailureListener(e -> {
                                            });
                                } else {
                                    db.collection("favorites")
                                            .document(String.valueOf(documentId))
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                favBtn.setBackgroundResource(R.drawable.ic_favorite_shadow);
                                                isFavorite.set(false);
                                                recipeItems.remove(recipe);
                                                notifyDataSetChanged();
                                            });
                                }
                            }
                        });
            });
        }
    }
}
