package com.apps.skimani.afyafood.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.apps.skimani.afyafood.api.RestClient
import com.apps.skimani.afyafood.database.AfyaDb
import com.apps.skimani.afyafood.database.FoodItem
import com.apps.skimani.afyafood.database.Meal
import com.apps.skimani.afyafood.models.FoodResponse
import com.apps.skimani.afyafood.models.InstantFoodItemResponse
import com.apps.skimani.afyafood.utils.Utils
import com.apps.skimani.afyafood.utils.safeApiCall
import com.apps.skimani.foodie.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

class AfyaRepository(private val database: AfyaDb) {

//    private var _foodItemTemp = MutableLiveData<List<FoodItem>>()
//    val foodItemTemp: LiveData<List<FoodItem>>
//        get() = database.foodItem.getItems()

    /**
     * Use SafeApiCall to map the network response
     *
     * @param query
     */
    suspend fun getInstantfood(query: String) = safeApiCall(
        call = { getInstanttems(query) },
        errorMessage = "Error occurred"
    )
    /**
     * Fetch instant food items from newtork
     *
     * @param query
     * @return
     */
    private suspend fun getInstanttems(query: String): NetworkResult<InstantFoodItemResponse> {
        val response = RestClient.apiService.fetchInstantItems(query)
        return when {
            response.isSuccessful -> {
                Timber.d("repo Data ${response.body()}")
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                Timber.d("repo error ${response.code()}")
                NetworkResult.Error(IOException("Error fetching the food"))
            }
        }
    }

    /**
     * Use SafeApiCall to map the network response
     *
     * @param query
     */
    suspend fun getfoodItem(query: String) = safeApiCall(
        call = { getFoodtems(query) },
        errorMessage = "Error occurred"
    )

    /**
     * Fetch food items from newtork
     *
     * @param query
     * @return
     */
    private suspend fun getFoodtems(query: String): NetworkResult<FoodResponse> {
        val jsObj = JSONObject()
        jsObj.put("query", query)
        val response = RestClient.apiService.listFoodItem(Utils.getRequestBody(jsObj))
        return when {
            response.isSuccessful -> {
                Timber.d("repo Data ${response.body()}")
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                Timber.d("repo error ${response.code()}")
                NetworkResult.Error(IOException("usage limits exceeded"))
//                NetworkResult.Error(IOException(": Today Usage limits exceeded"))
            }
        }
    }


    /**
     * Return a list of foodItems to be displayed
     */

    val foodItems: LiveData<List<FoodItem>> =
        Transformations.map(database.foodItem.getItems()) {
            it.reversed()
        }

    /**
     * Get food items
     *
     * @return
     */
    suspend fun fetchValue(): List<FoodItem>? {
        return withContext(Dispatchers.IO) {
            database.foodItem.getItemsValue()
        }
    }

    /**
     * Fetch All meals
     *
     * @return
     */
    suspend fun fetchAllMeals(): List<Meal>? {
        return withContext(Dispatchers.IO) {
            database.foodItem.getAllMeals()
        }
    }

    /**
     * Fetch meal by day
     *
     * @param query
     * @return List<Meal>
     */
    suspend fun fetchMealsByDay(query: String): List<Meal>? {
        return withContext(Dispatchers.IO) {
            database.foodItem.getMealsByDay(query)
        }
    }

    /**
     * Store the food items temporarily in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the food items for use, observe [foodItems]
     */
    suspend fun saveFoodItem(foodItem: FoodItem) {
        withContext(Dispatchers.IO) {
            database.foodItem.insertAll(foodItem)
            Timber.d("Fetch DB ${database.foodItem.getItems().value}")
        }
    }

    /**
     *Store the user mealin Room DB
     * @param meal
     */
    suspend fun saveAMeal(meal: Meal) {
        withContext(Dispatchers.IO) {
            database.foodItem.saveMeal(meal)
            Timber.d("Fetch DB ${database.foodItem.getAllMeals()}")
        }
    }

    /**
     *Delete the food item from Room DB
     * @param meal
     */
    suspend fun deleteFoodItem(foodItem: ArrayList<Int>):Int {
       return withContext(Dispatchers.IO) {
            Timber.d("DELETE Food item")
           database.foodItem.deleteFoodItem(foodItem)
        }
    }

}