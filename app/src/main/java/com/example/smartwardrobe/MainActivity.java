package com.example.smartwardrobe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public ArrayList<String> categories = new ArrayList<>();
    public ArrayList<String> availableCategories = new ArrayList<>();
    public BufferedReader reader;
    static AppDatabase clothingCollection;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clothingCollection= Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "clothingCollection").allowMainThreadQueries().build();

        //read category labels from txt file to be used amoung activites
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(("labels.txt"))));
            String line = reader.readLine();
            while(line!=null){
                categories.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCamera(View view) {
        Intent cameraIntent = new Intent(this, Camera.class);
        cameraIntent.putExtra("categories",categories);
        cameraIntent.putExtra("availableCategories",availableCategories);
        this.startActivity(cameraIntent);
    }

    public void startOutfitGenerator(View view) {
        Intent outfitIntent = new Intent(this, DisplayWeather.class);
        this.startActivity(outfitIntent);
    }

    public void viewWardrobe(View view){
        availableCategories = (ArrayList<String>)clothingCollection.clothingItemDao().getAvailableCategories();
        Log.d("info",""+availableCategories.size());
        if(availableCategories == null){
            Toast toast = Toast.makeText(this,"Wardrobe is empty", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Intent wardrobeIntent = new Intent(this, WardrobeActivity.class);
            wardrobeIntent.putExtra("availableCategories", availableCategories);
            this.startActivity(wardrobeIntent);
        }
    }
}