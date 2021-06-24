package com.apps.skimani.afyafood.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


class FoodItemConverter {
        private val gson = Gson()

        @TypeConverter
        fun stringToList(data: String?): List<FoodItem> {
            if (data == null) {
                return Collections.emptyList()
            }

            val listType = object : TypeToken<List<FoodItem>>() {

            }.type

            return gson.fromJson<List<FoodItem>>(data, listType)
        }

        @TypeConverter
        fun listToString(someObjects: List<FoodItem>): String {
            return gson.toJson(someObjects)
        }

}
class OtherServicesTypeConverter {
    private val gson = Gson()
    @TypeConverter
    fun stringToList(data: String?): ArrayList<HashMap<String, List<FoodItem>>> {
        if (data == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<HashMap<String, List<FoodItem>>>>() {

        }.type

        return gson.fromJson<ArrayList<HashMap<String, List<FoodItem>>>>(data, listType)
    }

    @TypeConverter
    fun listToString(objects: ArrayList<HashMap<String, List<FoodItem>>>): String {
        return gson.toJson(objects)
    }
}