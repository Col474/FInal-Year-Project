package com.example.smartwardrobe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ViewOutfitsActivity extends AppCompatActivity {
    ArrayList<String> outfitNames;
    ArrayAdapter<String> adapter;
    ListView outfitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }
        outfitNames = (ArrayList)MainActivity.outfitCollection.outfitDao().getOutfitNames();
        setContentView(R.layout.activity_view_outfits);
        outfitView = findViewById(R.id.outfitView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, outfitNames);
        outfitView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        outfitView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object outfit = outfitView.getItemAtPosition(position);
                String outfitName = outfit.toString();
                startViewOutfitItemsActivity(outfitName);
            }
        });
        outfitView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            public boolean onItemLongClick(final AdapterView<?> adapter1, View v, int position, long id) {
                final String selected = (String) outfitView.getItemAtPosition(position);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                MainActivity.outfitCollection.outfitDao().removeItemByName(selected);
                                int index = outfitNames.indexOf(selected);
                                outfitNames.remove(index);
                                adapter.notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewOutfitsActivity.this);
                builder.setMessage("Delete Outfit?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;
            }


        });
    }
    public void startViewOutfitItemsActivity(String outfitName) {
        Intent contentIntent = new Intent(this, ViewOutfitItemsActivity.class);
        contentIntent.putExtra("selectedOutfit", outfitName);
        this.startActivity(contentIntent);
    }

}