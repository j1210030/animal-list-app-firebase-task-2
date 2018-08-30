package com.android.accenture.knowme;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.animalsfb.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Query childRef;
    private FirebaseRecyclerAdapter<Animal, AnimalHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private FirebaseDatabase mFirebaseDatabaseInstance;
    private FloatingActionButton mFab;

    Animal animal;
    int itemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecycler = (RecyclerView) findViewById(R.id.fb_recycler_view);
        mRecycler.setHasFixedSize(true);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        mFirebaseDatabaseInstance = FirebaseDatabase.getInstance();
        // mFirebaseDatabaseInstance.setPersistenceEnabled(true);
        mDatabase = mFirebaseDatabaseInstance.getInstance().getReference();
        childRef = mDatabase.child(Constant.ANIMALS_CONST).child(Constant.LIST_ITEMS_CONST);

        mAdapter = new FirebaseRecyclerAdapter<Animal, AnimalHolder>(Animal.class, R.layout.custom_layout,
                AnimalHolder.class, childRef) {
            @Override
            protected void populateViewHolder(final AnimalHolder animalHolder, final Animal animal, final int position) {

                // animal name
                animalHolder.animalName.setText(animal.getName() + Constant.LEFT_PARENTHESIS + animal.getScientific_name() + Constant.RIGHT_PARENTHESIS);

                // habitat
                animalHolder.habitat.setText(animal.getHabitat());

                FirebaseStorageRequester firebaseStorageRequester = new FirebaseStorageRequester();

                // animal image
                animalHolder.animalImage = firebaseStorageRequester.getImageViewFromFirebaseStorage(animal.getThumbnail(), getApplicationContext(), animalHolder.animalImage);

                // foodtype
                animalHolder.foodtype = firebaseStorageRequester.getImageViewFromFirebaseStorage(animal.getFoodtype(), getApplicationContext(), animalHolder.foodtype);

            }
        };
        mRecycler.setAdapter(mAdapter);

        mFab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animal = new Animal();
                itemCount = mAdapter.getItemCount();
                animal.setThumbnail(Constant.DEFAULT_IMAGE);
                setIntentValue(v, Constant.PUSH_FAB_SELECTION);
            }
        });


        mRecycler.addOnItemTouchListener(
            new RecyclerItemClickListener(this, mRecycler ,new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    animal = new Animal();
                    itemCount = mAdapter.getItemCount();
                    setAnimal(position);
                    setIntentValue(view, Constant.ITEM_TOUCH_SELECTION);
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    // do whatever
                }
            })
        );
    }

    public void setIntentValue(View view, String selection){
        Intent intent = new Intent(view.getContext(), CrudActivity.class);

        intent.putExtra(Constant.ANIMAL_INTENT, animal);
        intent.putExtra(Constant.ITEM_COUNT_INTENT, itemCount);
        intent.putExtra(Constant.SELECTION_INTENT, selection);

        startActivity(intent);

    }

    public void setAnimal(int position){
        animal.setName(mAdapter.getItem(position).getName().toString());
        animal.setScientific_name(mAdapter.getItem(position).getScientific_name().toString());
        animal.setCategory(mAdapter.getItem(position).getCategory().toString());
        animal.setFood(mAdapter.getItem(position).getFood().toString());
        animal.setHabitat(mAdapter.getItem(position).getHabitat().toString());
        animal.setDescription(mAdapter.getItem(position).getDescription().toString());
        animal.setThumbnail(mAdapter.getItem(position).getThumbnail().toString());
        animal.setId(mAdapter.getItem(position).getId().toString());

    }
}