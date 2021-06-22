package com.apps.skimani.afyafood.database

import androidx.room.Entity
import androidx.room.PrimaryKey




@Entity
class Meal(
    val name: String,
    val time: String,
    val day: String,
    val totalCalories: String,
    val serving: String
    ){
    @PrimaryKey(autoGenerate = true)
    var mealId:Int=0
}
