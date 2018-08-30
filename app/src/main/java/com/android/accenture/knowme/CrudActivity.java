package com.android.accenture.knowme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.animalsfb.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by ykashiwagi on 7/31/17.
 */

public class CrudActivity extends AppCompatActivity {

    Animal animal;

    String selection;
    String No;
    int itemCount;

    TextView animalNameTextView;
    TextView descriptionTextView;
    TextView animalScientificNameTextView;
    TextView habitatTextView;

    Spinner categorySpinner;
    Spinner foodSpinner;

    ImageView thumbnailImageView;

    String animalNameTextViewTrim;
    String descriptionTextViewTrim;
    String animalScientificNameTextViewTrim;
    String habitatTextViewTrim;
    String categorySpinnerToString;
    String foodSpinnerToString;

    ProgressDialog pd;
    Uri filePath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(Constant.STORAGE_URL);

    Button chooseImg, uploadImg, returnButton, deleteButton, saveButton;
    int PICK_IMAGE_REQUEST = 111;

    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseDatabaseInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        readById();
        returnButton();
        uploadFile2Storage();
        removeAnimal();

        if(selection.equals(Constant.ITEM_TOUCH_SELECTION)){
            // operation when push save button for edit animal
            edit();

        } else if(selection.equals(Constant.PUSH_FAB_SELECTION)){
            // operation when push save button for add new animal
            addNew();

        }
    }

    // This will read the Firebase databse and only fetch the Animal data which matches the id2Get value
    public Animal readById(){
        initView();
        initViewButton();
        getIntentValue();
        setDisplayValue();

        return animal;
    }

    public void returnButton(){
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);

            }
        });
    }

    // Will upload new file to the firebase storage
    public boolean uploadFile2Storage(){
        pd = new ProgressDialog(this);
        pd.setMessage(Constant.UPLOADING_MESSAGE);

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType(Constant.IMAGES_TYPE);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, Constant.SELECT_IMAGE_MESSAGE), PICK_IMAGE_REQUEST);
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath != null) {
                    pd.show();

                    StorageReference childRef = storageRef.child(Constant.IMAGES_FILEPATH + Constant.ANIMALS_FILEPATH).child(No + Constant.JPEG);
                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            animal.setThumbnail(No + Constant.JPEG);
                            Toast.makeText(CrudActivity.this, Constant.UPLOAD_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(CrudActivity.this, Constant.UPLOAD_FAILED_MESSAGE + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(CrudActivity.this, Constant.SELECT_AN_IMAGE_MESSAGE, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                thumbnailImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // This will delete the Animal data from Firebase database with id which matched id2del value.
    public boolean removeAnimal (){
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = mFirebaseDatabaseInstance.getInstance().getReference();

                Query animalQuery = mDatabase.child(Constant.ANIMALS_CONST).child(Constant.LIST_ITEMS_CONST).orderByChild(Constant.ID_CONST).equalTo(animal.getId());
                animalQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot animalSnapshot: dataSnapshot.getChildren()) {
                            animalSnapshot.getRef().removeValue();

                            valueClear();

                            //displaying a success toast
                            Toast.makeText(CrudActivity.this, Constant.DELETE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(CrudActivity.this, Constant.DELETE_FAILED_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        removeFileFromStorage();

        return false;
    }

    // This will add new Animal record to database.
    public void addNew(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = mFirebaseDatabaseInstance.getInstance().getReference();

                toStringItems();

                if(animalNameTextViewTrim.equals("")|| descriptionTextViewTrim.equals("") ||
                        animalScientificNameTextViewTrim.equals("") || habitatTextViewTrim.equals("")){
                    Toast.makeText(CrudActivity.this, Constant.WARNING_MESSAGE, Toast.LENGTH_SHORT).show();
                }else {
                    setAnimal();

                    mDatabase.child(Constant.ANIMALS_CONST).child(Constant.LIST_ITEMS_CONST).push().setValue(animal);

                    valueClear();

                    //displaying a success toast
                    Toast.makeText(CrudActivity.this, Constant.SAVE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    // This will update the existing Animal data with the supplied animal2Updt (match with id)
    public void edit (){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = mFirebaseDatabaseInstance.getInstance().getReference();
                Query applesQuery = mDatabase.child(Constant.ANIMALS_CONST).child(Constant.LIST_ITEMS_CONST).orderByChild(Constant.ID_CONST).equalTo(animal.getId());
                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {

                            toStringItems();

                            if(animalNameTextViewTrim.equals("")|| descriptionTextViewTrim.equals("") ||
                                    animalScientificNameTextViewTrim.equals("") || habitatTextViewTrim.equals("")){

                                Toast.makeText(CrudActivity.this, Constant.WARNING_MESSAGE, Toast.LENGTH_SHORT).show();
                            }else {
                                itemsPut(appleSnapshot);

                                valueClear();

                                //displaying a success toast
                                Toast.makeText(CrudActivity.this, Constant.SAVE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    // This will remove file from Firebase storage After Add/Edit operation Must display the Anmial list page.
    public boolean removeFileFromStorage(){
        // Create a reference to the file to delete
        StorageReference desertRef = storageRef.child(Constant.IMAGES_FILEPATH + Constant.ANIMALS_FILEPATH + animal.getThumbnail());

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
            return false;
    }

    public void initView(){
        animalNameTextView = (TextView)findViewById(R.id.animal_name);
        animalScientificNameTextView = (TextView)findViewById(R.id.animal_scientific_name);
        categorySpinner = (Spinner)findViewById(R.id.animal_category);
        foodSpinner = (Spinner)findViewById(R.id.animal_food);
        habitatTextView  = (TextView)findViewById(R.id.animal_habitat);
        descriptionTextView = (TextView)findViewById(R.id.animal_description);
        thumbnailImageView = (ImageView)findViewById(R.id.animal_thumbnail);

    }

    public void initViewButton(){
        chooseImg = (Button)findViewById(R.id.chooseImg);
        uploadImg = (Button)findViewById(R.id.uploadImg);
        returnButton = (Button)findViewById(R.id.return_button);
        deleteButton = (Button)findViewById(R.id.delete_button);
        saveButton = (Button)findViewById(R.id.save_button);

    }

    public void getIntentValue(){
        Intent intent = getIntent();
        animal = (Animal)getIntent().getSerializableExtra(Constant.ANIMAL_INTENT);
        itemCount = intent.getIntExtra(Constant.ITEM_COUNT_INTENT,0);
        selection = intent.getStringExtra(Constant.SELECTION_INTENT);

    }

    public void setDisplayValue(){
        animalNameTextView.setText(animal.getName());
        animalScientificNameTextView.setText(animal.getScientific_name());
        habitatTextView.setText(animal.getHabitat());
        descriptionTextView.setText(animal.getDescription());

        if(animal.getCategory() != null) {
            if(animal.getCategory().equals(Constant.AMPHIBIANS_CONST)){
                categorySpinner.setSelection(0);

            }else if(animal.getCategory().equals(Constant.MAMMALS_CONST)){
                categorySpinner.setSelection(1);

            }
        }

        if(animal.getFoodtype() != null) {
            if(animal.getFoodtype().equals(Constant.CARNIVOROUS_CONST)){
                foodSpinner.setSelection(0);

            }else if(animal.getFoodtype().equals(Constant.HARNIVOROUS_CONST)){
                foodSpinner.setSelection(1);

            }
        }

        FirebaseStorageRequester firebaseStorageRequester = new FirebaseStorageRequester();
        thumbnailImageView = firebaseStorageRequester.getImageViewFromFirebaseStorage(animal.getThumbnail(), this, thumbnailImageView);

        int ID = itemCount + 1;
        No = String.format(Constant.FIRST_CHARACTER_ZERO, ID);

    }

    public void valueClear(){
        animalNameTextView.setText("");
        animalScientificNameTextView.setText("");
        habitatTextView.setText("");
        descriptionTextView.setText("");
        categorySpinner.setSelection(0);
        foodSpinner.setSelection(0);
    }

    public void toStringItems(){
        animalNameTextViewTrim = animalNameTextView.getText().toString().trim();
        descriptionTextViewTrim = descriptionTextView.getText().toString().trim();
        animalScientificNameTextViewTrim = animalScientificNameTextView.getText().toString().trim();
        habitatTextViewTrim = habitatTextView.getText().toString().trim();
        categorySpinnerToString = categorySpinner.getSelectedItem().toString();
        foodSpinnerToString = foodSpinner.getSelectedItem().toString();

    }

    public void setAnimal(){
        animal.setName(animalNameTextViewTrim);
        animal.setScientific_name(animalScientificNameTextViewTrim);
        animal.setHabitat(habitatTextViewTrim);
        animal.setCategory(categorySpinnerToString);
        animal.setFood(foodSpinnerToString);
        animal.setFoodtype(foodSpinnerToString.toLowerCase().substring(0,5) + Constant.PNG);
        animal.setDescription(descriptionTextViewTrim);
        animal.setId(No);

    }

    public void itemsPut(DataSnapshot animalSnapshot){
        Map<String, Object> items = new HashMap<>();

        items.put(Constant.NAME_KEY, animalNameTextViewTrim);
        items.put(Constant.CATEGORY_KEY, categorySpinnerToString);
        items.put(Constant.SCIENTIFIC_NAME_KEY, animalScientificNameTextViewTrim);
        items.put(Constant.HABITAT_KEY, habitatTextViewTrim);
        items.put(Constant.FOOD_KEY, foodSpinnerToString);
        items.put(Constant.DESCRIPTION_KEY, descriptionTextViewTrim);
        items.put(Constant.THUMBNAIL_KEY, animal.getThumbnail());

        animalSnapshot.getRef().updateChildren(items);

    }
}