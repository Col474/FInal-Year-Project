package com.example.smartwardrobe;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClothingItemDao {
    @Query("SELECT DISTINCT category FROM clothingcollection")
    List<String> getAvailableCategories();

    @Query("SELECT * FROM clothingCollection WHERE itemName = :name")
    ClothingItem getItemByName(String name);

    @Query("SELECT * FROM clothingCollection WHERE itemName = :name")
    int getItemByNameInt(String name);

    @Query("SELECT * FROM clothingCollection WHERE category = :category")
    List<ClothingItem> loadAllByCategory(String category);

    @Query("SELECT itemName FROM clothingCollection WHERE category = :category")
    List<String>loadNameByCategory(String category);

    @Query("SELECT * FROM clothingcollection WHERE category = :category ORDER BY RANDOM() LIMIT 1")
    ClothingItem getRandItemFromCat(String category);

    @Query("SELECT * FROM clothingcollection WHERE category = :category AND color = :color ORDER BY RANDOM() LIMIT 1")
    ClothingItem getItemByCategoryColor(String category,String color);

    @Query("SELECT * FROM clothingCollection WHERE category IN (:category)")
    List<ClothingItem> loadAllByCategories(List<String> category);

    @Query("SELECT color FROM clothingCollection WHERE category IN (:categories)")
    List<String> loadColorsByCategory(List<String> categories);

    @Query("SELECT * FROM clothingCollection WHERE category IN (:category) AND color = :color  ORDER BY RANDOM() LIMIT 1")
    ClothingItem getItemByCatColor(List<String> category,String color);

    @Query("DELETE FROM clothingCollection WHERE itemname = :itemName")
    void RemoveItemByName(String itemName);



    @Insert
    void insert(ClothingItem clothingItem);

    @Delete
    void delete(ClothingItem clothingItem);
}