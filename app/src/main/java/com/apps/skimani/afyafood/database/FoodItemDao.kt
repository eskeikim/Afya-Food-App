package com.apps.skimani.afyafood.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FoodItemDao {
    @Query("select * from FoodItem")
    fun getItems(): LiveData<List<FoodItem>>

    @Query("select * from FoodItem")
    suspend fun getItemsValue(): List<FoodItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg foodItem: FoodItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMeal(vararg meal: Meal)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun saveMealAndFood(vararg meal: MealAndFood)

    @Query("select * from Meal")
    suspend fun getAllMeals(): List<Meal>?

    @Query("select * from Meal WHERE day=:dayQuery")
    suspend fun getMealsByDay(dayQuery:String): List<Meal>?

    @Query("DELETE from fooditem where id in (:foodItem)")
    suspend fun deleteFoodItem(foodItem: ArrayList<Int>):Int

//    @Transaction
//    @Query("SELECT * FROM meal")
//    fun getMealWithFoodItems():List<MealAndFood>


}