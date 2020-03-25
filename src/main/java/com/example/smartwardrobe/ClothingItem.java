package com.example.smartwardrobe;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Locale;

@Entity(tableName = "ClothingCollection")
public class ClothingItem {

    @PrimaryKey
    @NonNull
    public String itemName;

    @ColumnInfo(name = "image")
    public String image;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "color")
    public String color;

    public ClothingItem(String itemName,String image,String category,String color){
        this.itemName = itemName;
        this.image = image;
        this.category = category;
        this.color = color;
    }
}
