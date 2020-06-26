package pt.ipleiria.estg.foodzam.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.foodzam.R;
import pt.ipleiria.estg.foodzam.model.Recipe;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {


    private Context mContext;
    private int mResource;

    private static class ViewHolder {
        TextView title;
        TextView description;
        ImageView image;
    }

    public RecipeListAdapter(Context context, int resource, List<Recipe> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String title = getItem(position).getTitle();
        String description = getItem(position).getTitle(); //PARA TESTAR
        String image = getItem(position).getImage();

        Recipe recipe = new Recipe(title,description,image);

        final View result;

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.textViewTitleRow);
            holder.description = (TextView) convertView.findViewById(R.id.textViewDescriptionRow);
            holder.image = (ImageView) convertView.findViewById(R.id.imageRow);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.title.setText(recipe.getTitle());
        holder.description.setText(recipe.getTitle());
        Glide.with(mContext).load(recipe.getImage()).into(holder.image);


        return convertView;
    }
}
