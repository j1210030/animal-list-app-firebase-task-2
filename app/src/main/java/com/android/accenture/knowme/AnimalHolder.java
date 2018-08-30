package com.android.accenture.knowme;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.animalsfb.R;

/**
 * Created by ykashiwagi on 6/21/17.
 */

public class AnimalHolder extends RecyclerView.ViewHolder {

    public ImageView animalImage;
    public TextView animalName;
    public TextView habitat;
    public ImageView foodtype;

    public AnimalHolder(View itemView) {
        super(itemView);

        animalImage = (ImageView) itemView.findViewById(R.id.animal_image);
        animalName = (TextView) itemView.findViewById(R.id.animal_name);
        habitat = (TextView) itemView.findViewById(R.id.habitat);
        foodtype = (ImageView) itemView.findViewById(R.id.foodtype);

    }


}
