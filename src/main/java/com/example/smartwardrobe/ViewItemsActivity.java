package com.example.smartwardrobe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class ViewItemsActivity extends AppCompatActivity {
    ArrayList<ClothingItem> items = new ArrayList<>();
    ListView itemList;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);
        String category = (String) getIntent().getSerializableExtra("selectedCategory"); //get categories array
        labels = (ArrayList)MainActivity.clothingCollection.clothingItemDao().loadNameByCategory(category);
        items = (ArrayList) MainActivity.clothingCollection.clothingItemDao().loadAllByCategory(category);


        for (int i = 0; i < items.size(); i++) {
            String path = items.get(i).image;
            String label = items.get(i).itemName;
            try {
                File f = new File(path + "/" + label + ".jpg");
                bitmaps.add(BitmapFactory.decodeStream(new FileInputStream(f)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        itemList = findViewById(R.id.itemsList);
        final ClothingItemAdapter adapter = new ClothingItemAdapter(this, labels, bitmaps);
        itemList.setAdapter(adapter);

        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            public boolean onItemLongClick(final AdapterView<?> adapter1, View v, int position, long id) {

                TextView textview1 = (TextView) v.findViewById(R.id.textView1);
                final String selected = textview1.getText().toString();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                MainActivity.clothingCollection.clothingItemDao().RemoveItemByName(selected);
                                int index = labels.indexOf(selected);
                                labels.remove(index);
                                bitmaps.remove(index);
                                adapter.notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewItemsActivity.this);
                builder.setMessage("Delete Item from Wardrobe?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;
            }


        });
    }
}
