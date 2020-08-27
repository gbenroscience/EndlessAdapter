package com.itis.libs.examples;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itis.libs.R;


public abstract class ModelAdapter extends EndlessAdapter<Model> {


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {




        ViewHolder holder = null;


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent
                    .getContext());

            holder = new ViewHolder();

            convertView = inflater.inflate(
                    R.layout.list_cell, parent, false);


            holder.nameView = convertView.findViewById(R.id.cell);

            // minimize the default image.
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Model model = getItem(position);

        try {
            holder.nameView.setText(model.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.getView(position,convertView,parent);
    }



    static class ViewHolder {

        TextView nameView;
    }
}
