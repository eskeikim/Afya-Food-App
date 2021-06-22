package com.apps.skimani.afyafood.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

//@ProvidedTypeConverter
class FoodItemConverter {
    companion object {
        var gson: Gson = Gson()

        @TypeConverter
        fun stringToFoodItemObject(data: String?): List<FoodItem?>? {
            if (data == null) {
                return Collections.emptyList()
            }
            val listType: Type = object : TypeToken<List<FoodItem?>?>() {}.getType()
            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun FoodItemOjectToToString(someObjects: List<FoodItem?>?): String? {
            return gson.toJson(someObjects)
        }
    }
}