package com.example.smartwardrobe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class ViewOutfitItemsActivity extends AppCompatActivity {
    ArrayList<ClothingItem> items = new ArrayList<>();
    ListView itemList;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_outfit_items);
        String outfitName = (String) getIntent().getSerializableExtra("selectedOutfit"); //get categories array
        Outfit outfit = MainActivity.outfitCollection.outfitDao().loadOutfitItems(outfitName);

        if(outfit.top !=null){
            items.add(MainActivity.clothingCollection.clothingItemDao().getItemByName(outfit.top));
        }
        if(outfit.topper !=null){
            items.add(MainActivity.clothingCollection.clothingItemDao().getItemByName(outfit.topper));
        }
        if(outfit.jacket !=null){
            items.add(MainActivity.clothingCollection.clothingItemDao().getItemByName(outfit.jacket));
        }
        if(outfit.bottom !=null){
            items.add(MainActivity.clothingCollection.clothingItemDao().getItemByName(outfit.bottom));
        }
        if(outfit.shoes !=null){
            items.add(MainActivity.clothingCollection.clothingItemDao().getItemByName(outfit.shoes));
        }
        for (int i = 0; i < items.size(); i++) {
            String path = items.get(i).image;
            String label = items.get(i).itemName;
            try {
                File f = new File(path + "/" + label + ".jpg");
                bitmaps.add(BitmapFactory.decodeStream(new FileInputStream(f)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            labels.add(label);
        }

        itemList = findViewById(R.id.OutfitItemsList);
        final ClothingItemAdapter adapter = new ClothingItemAdapter(this, labels, bitmaps);
        itemList.setAdapter(adapter);
    }
}
