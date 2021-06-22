package com.apps.skimani.afyafood.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [FoodItem::class,Meal::class], version = 1)
abstract class AfyaDb : RoomDatabase() {
    abstract val foodItem: FoodItemDao
}

private lateinit var INSTANCE: AfyaDb

fun getDatabase(context: Context): AfyaDb {
    synchronized(AfyaDb::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext, AfyaDb::class.java, "afyadb")
                .build()
        }
    }
    return INSTANCE
}