package com.apps.skimani.afyafood.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName


@Entity
class Meal(
    val name: String,
    val time: String,
    val day: String,
    val totalCalories: String,
    val serving: String,
//    @TypeConverters(FoodItemConverter::class)
//    @SerializedName("otherServices") var otherServices: ArrayList<HashMap<String, FoodItem>> = arrayListOf(),

//    @TypeConverters(FoodItemConverter::class)
//    var foodItem: List<FoodItem> = ArrayList<FoodItem>()

    ){
    @PrimaryKey(autoGenerate = true)
    var mealId:Int=0
}
