package pt.ipleiria.estg.foodzam.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import pt.ipleiria.estg.foodzam.R;
import pt.ipleiria.estg.foodzam.model.Ingredient;

public class IngredientsAdapter extends ArrayAdapter<Ingredient> {

    private Context mContext;
    private int mResource;

    private static class ViewHolder {
        TextView title;
    }

    public IngredientsAdapter(@NonNull Context context, int resource, List<Ingredient> ingredients) {
        super(context, resource, ingredients);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String title = getItem(position).getOriginal();

        Ingredient ingredient = new Ingredient(title);

        IngredientsAdapter.ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new IngredientsAdapter.ViewHolder();
            holder.title = convertView.findViewById(R.id.textViewIngredientTitle);

            convertView.setTag(holder);
        }
        else{
            holder = (IngredientsAdapter.ViewHolder) convertView.getTag();
        }

        holder.title.setText(ingredient.getOriginal());

        return convertView;
    }
}
