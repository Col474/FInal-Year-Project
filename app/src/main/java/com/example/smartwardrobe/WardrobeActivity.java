package com.example.smartwardrobe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class WardrobeActivity extends AppCompatActivity {
    ArrayList<String> categories;
    ArrayAdapter<String> adapter;
    ListView wardrobeContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }
        categories = (ArrayList<String>) getIntent().getSerializableExtra("availableCategories"); //get categories array
        setContentView(R.layout.activity_wardrobe);
        wardrobeContents = findViewById(R.id.wardrobeContents);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        wardrobeContents.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        wardrobeContents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object category = wardrobeContents.getItemAtPosition(position);
                String selectedCategory = category.toString();
                startViewItemsActivity(selectedCategory);
            }
        });

    }
    public void startViewItemsActivity(String category) {
        Intent contentIntent = new Intent(this, ViewItemsActivity.class);
        contentIntent.putExtra("selectedCategory", category);
        this.startActivity(contentIntent);
    }

}