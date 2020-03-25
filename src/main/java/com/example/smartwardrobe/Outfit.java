package com.example.smartwardrobe;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Locale;

@Entity(tableName = "outfitCollection")
public class Outfit {

    @PrimaryKey
    @NonNull
    public String outfitName;

    @ColumnInfo(name = "top")
    public String top;

    @ColumnInfo(name = "topper")
    public String topper;

    @ColumnInfo(name = "jacket")
    public String jacket;

    @ColumnInfo(name = "bottom")
    public String bottom;

    @ColumnInfo(name = "shoes")
    public String shoes;

    public Outfit(){
    }
    public void setOutfitName(String outfitName){
        this.outfitName = outfitName;
    }
    public void setTop(String top){
        this.top = top;
    }
    public void setTopper(String topper){
        this.topper = topper;
    }
    public void setJacket(String jacket){
        this.jacket = jacket;
    }
    public void setBottom(String bottom){
        this.bottom = bottom;
    }
    public void setShoes(String Shoes){
        this.shoes = shoes;
    }
}
