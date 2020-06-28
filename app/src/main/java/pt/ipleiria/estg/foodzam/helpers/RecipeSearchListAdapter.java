package pt.ipleiria.estg.foodzam.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

import pt.ipleiria.estg.foodzam.R;
import pt.ipleiria.estg.foodzam.model.Recipe;

public class RecipeSearchListAdapter extends ArrayAdapter<Recipe> {


    private Context mContext;
    private int mResource;

    private static class ViewHolder {
        TextView title;
        ImageView image;
    }

    public RecipeSearchListAdapter(Context context, int resource, List<Recipe> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String title = getItem(position).getTitle();
        String image = getItem(position).getImage();

        Recipe recipe = new Recipe(title, image);

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.title = convertView.findViewById(R.id.textViewTitleRow);
            holder.image = convertView.findViewById(R.id.imageRow);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(recipe.getTitle());
        Glide.with(mContext).load(recipe.getImage()).into(holder.image);

        return convertView;
    }
}
