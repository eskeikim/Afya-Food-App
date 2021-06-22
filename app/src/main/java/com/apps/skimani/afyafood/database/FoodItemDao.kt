package com.apps.skimani.afyafood.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FoodItemDao {
    @Query("select * from FoodItem")
    fun getItems(): LiveData<List<FoodItem>>

    @Query("select * from FoodItem")
    suspend fun getItemsValue(): List<FoodItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: FoodItem)
}