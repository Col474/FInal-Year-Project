package com.example.smartwardrobe;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ClothingItem.class,Outfit.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ClothingItemDao clothingItemDao();
    public abstract OutfitDao outfitDao();
}
