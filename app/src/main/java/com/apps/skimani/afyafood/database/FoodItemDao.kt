package com.apps.skimani.afyafood.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FoodItemDao {
    /**
     * fetch foot items
     *
     * @return
     */
    @Query("select * from FoodItem")
    fun getItems(): LiveData<List<FoodItem>>

    /**
     * test
     *
     * @return
     */
    @Query("select * from FoodItem")
    suspend fun getItemsValue(): List<FoodItem>?

    /**
     * TODOInsert food items to roomdb
     *
     * @param foodItem
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg foodItem: FoodItem)

    /**
     * Insert user meal to room db
     *
     * @param meal
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMeal(vararg meal: Meal)

    /**
     * Fetch all meals
     *
     * @return
     */
    @Query("select * from Meal")
    suspend fun getAllMeals(): List<Meal>?

    /**
     * Fetch meals by day
     *
     * @param dayQuery
     * @return
     */
    @Query("select * from Meal WHERE day=:dayQuery")
    suspend fun getMealsByDay(dayQuery:String): List<Meal>?

    /**
     * Delete temporaly food items after the meal is inserted to db
     *
     * @param foodItem
     * @return
     */
    @Query("DELETE from fooditem where id in (:foodItem)")
    suspend fun deleteFoodItem(foodItem: ArrayList<Int>):Int

}