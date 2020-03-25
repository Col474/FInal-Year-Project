package com.example.smartwardrobe;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OutfitDao {

    @Query("SELECT outfitName FROM outfitCollection")
    List<String> getOutfitNames();

    @Query("SELECT * FROM outfitCollection WHERE outfitName = :name")
    Outfit loadOutfitItems(String name);

    @Query("DELETE FROM outfitCollection WHERE outfitName = :outfitName")
    void removeItemByName(String outfitName);

    @Insert
    void insert(Outfit outfit);
}
