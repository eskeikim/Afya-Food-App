package com.apps.skimani.afyafood.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

//@Entity
class MealAndFood(
    @Embedded
    val meal: Meal,
    @Relation(
    parentColumn="mealid",
        entityColumn = "id"
    )
    val foodItem:List<Meal>
)