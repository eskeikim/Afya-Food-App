package com.apps.skimani.afyafood.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FoodItem (
    val foodName:String,
    val brandName:String,
    val calories:String,
    val servingQuantityString: String,
    val image:String
)
{
    @PrimaryKey(autoGenerate = true)
    var id:Int=0
}