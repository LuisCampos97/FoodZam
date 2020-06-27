package pt.ipleiria.estg.foodzam;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private ArrayList<RecipeItem> recipeItems;
    private Context context;
    private FavDB favDB;

    public RecipeAdapter(ArrayList<RecipeItem> recipeItems, Context context){
        this.recipeItems = recipeItems;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        favDB = new FavDB(context);
        //create table on first
        SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if(firstStart){
            createTableOnFirstStart();
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

        return new ViewHolder(view);
    }

    private void createTableOnFirstStart() {
        favDB.insertEmpty();
        SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("firstStart",false);
        editor.apply();
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        final RecipeItem recipeItem = recipeItems.get(position);
        
        readCursorData(recipeItem,holder);
        holder.imageView.setImageResource(recipeItem.getImageResource());
        holder.titleTextView.setText(recipeItem.getTitle());
        
    }

    private void readCursorData(RecipeItem recipeItem,ViewHolder viewHolder) {
        Cursor cursor = favDB.read_all_data(recipeItem.getKey_id());
        SQLiteDatabase db = favDB.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String item_fav_status = cursor.getString(cursor.getColumnIndex(FavDB.FAVORITE_STATUS));
                recipeItem.setFavStatus(item_fav_status);

                //check fav status
                if(item_fav_status != null && item_fav_status.equals("1")){
                    viewHolder.favBtn.setBackgroundResource(R.drawable.ic_favorite_red);
                }else if(item_fav_status != null && item_fav_status.equals("0")){
                    viewHolder.favBtn.setBackgroundResource(R.drawable.ic_favorite_shadow);
                }
            }
        } finally {
            if(cursor != null && cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    @Override
    public int getItemCount() {
        return recipeItems.size();
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

            //add to fav button
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    RecipeItem recipeItem = recipeItems.get(position);

                    if(recipeItem.getFavStatus().equals("0")){
                        recipeItem.setFavStatus("1");
                        favDB.insertIntoTheDatabase(recipeItem.getTitle(),recipeItem.getImageResource(),
                                recipeItem.getKey_id(),recipeItem.getFavStatus());
                        favBtn.setBackgroundResource(R.drawable.ic_favorite_red);
                    }else{
                        recipeItem.setFavStatus("0");
                        favDB.remove_fav(recipeItem.getKey_id());
                        favBtn.setBackgroundResource(R.drawable.ic_favorite_shadow);
                    }
                }
            });
        }
    }
}
