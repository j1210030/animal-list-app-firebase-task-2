package com.android.accenture.knowme;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.android.accenture.knowme.Constant.ANIMALS_FILEPATH;
import static com.android.accenture.knowme.Constant.IMAGES_FILEPATH;
import static com.android.accenture.knowme.Constant.STORAGE_URL;

/**
 * Created by ykashiwagi on 7/12/17.
 */

public class FirebaseStorageRequester {
    FirebaseStorage storage;
    FirebaseStorageRequester(){
        storage = FirebaseStorage.getInstance();
    }

    public ImageView getImageViewFromFirebaseStorage(String imageName, Context customAdapter, ImageView imageView){

        // Create a reference to the file you want to download
        StorageReference storageRef = storage.getReferenceFromUrl(STORAGE_URL);
        StorageReference imageRef = storageRef.child(IMAGES_FILEPATH + ANIMALS_FILEPATH + imageName);

        // Load the image using Glide
        Glide.with(customAdapter)
                .using(new FirebaseImageLoader())
                .load(imageRef)
                .into(imageView);

        return imageView;

    }

}
