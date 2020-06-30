package pt.ipleiria.estg.foodzam.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import pt.ipleiria.estg.foodzam.R;

public class PredictIngredientsAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int mResource;

    public PredictIngredientsAdapter(@NonNull Context context, int resource, ArrayList<String> ingredients) {
        super(context, resource, ingredients);
        mContext = context;
        mResource = resource;
    }

    private static class ViewHolder {
        TextView title;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String title = getItem(position);

        PredictIngredientsAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new PredictIngredientsAdapter.ViewHolder();
            holder.title = convertView.findViewById(R.id.textViewPredictIngredientTitle);

            convertView.setTag(holder);
        } else {
            holder = (PredictIngredientsAdapter.ViewHolder) convertView.getTag();
        }

        holder.title.setText(title);

        return convertView;
    }
}

