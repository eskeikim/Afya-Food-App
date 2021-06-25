package com.apps.skimani.afyafood.ui.add

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apps.skimani.afyafood.database.FoodItem
import com.apps.skimani.afyafood.database.Meal
import com.apps.skimani.afyafood.database.getDatabase
import com.apps.skimani.afyafood.models.*
import com.apps.skimani.afyafood.repository.AfyaRepository
import com.apps.skimani.foodie.utils.NetworkResult
import kotlinx.coroutines.*
import timber.log.Timber

class AddMealViewModel(app: Application) : ViewModel() {

    private var _mealsSearch = MutableLiveData<NetworkResult<InstantFoodItemResponse>>()
    val mealsSearch: MutableLiveData<NetworkResult<InstantFoodItemResponse>>
        get() = _mealsSearch
    private var _mealsSearchTest = MutableLiveData<ArrayList<BrandedList>>()
    val mealsSearchTest: MutableLiveData<ArrayList<BrandedList>>
        get() = _mealsSearchTest

    private var _foodItem = MutableLiveData<NetworkResult<FoodResponse>>()
    val foodItem: MutableLiveData<NetworkResult<FoodResponse>>
        get() = _foodItem
//    private var _foodItemTemp = MutableLiveData<List<FoodItem>?>()
//    var foodItemTemp: LiveData<List<FoodItem>?>
//        get() = _foodItemTemp


    private var _foodItemTempValue = MutableLiveData<List<FoodItem>?>()
    val foodItemTempValue: LiveData<List<FoodItem>?>
        get() = _foodItemTempValue

    private var _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error
  private var _deleteFoodStatus = MutableLiveData<Int>()
    val deleteFoodStatus: LiveData<Int>
        get() = _deleteFoodStatus

    private var mealsJob = Job()
    private val uiScope = CoroutineScope(mealsJob + Dispatchers.IO)
    val dummyList = ArrayList<BrandedList>()
    private val database = getDatabase(app)
    private val afyaRepository = AfyaRepository(database)

    init {
       _deleteFoodStatus.value=0
        getInstantItemsTest("")
        getFoodItemRoomDB()
    }

//    val tempFoodItems = afyaRepository.foodItems

    /**
     * Get food items from roomdb via Coroutine backckground thread
     * and post the value to the _foodItemTempValue Mutablelivedata
     * this is observed by the fragment layout with the help of data binding binding adapters
     *
     */
    fun getFoodItemRoomDB() {
        uiScope.launch {
            val value = afyaRepository.fetchValue()
            Timber.e("VIEWMODEL ${value?.size} ")
            _foodItemTempValue.postValue(value)
        }
    }

    /**
     * Safely save a food item to room db via Coroutine IO thread
     *
     * @param foodItem
     */
    fun saveFoodItemRoomDB(foodItem: FoodItem) {
        uiScope.launch {
            afyaRepository.saveFoodItem(foodItem)
        }
    }

    /**
     *  Safely save a meal to room db via Coroutine IO thread
     *
     * @param meal
     */
    fun saveAMealRoomDB(meal: Meal) {
        uiScope.launch {
            afyaRepository.saveAMeal(meal)
        }
    }

    /**
     *Delete the food items after successfully meal insersion
     *
     * @param foodItem
     */
    fun deleteFoodItem(foodItem: ArrayList<Int>) {
         uiScope.launch {
          val status=afyaRepository.deleteFoodItem(foodItem)
             _deleteFoodStatus.postValue(status)
             Timber.e("Deleted $status")
        }
    }

    /**
     * Dummy data for instant items
     *
     * @param query
     */
    private fun getInstantItemsTest(query: String) {
        uiScope.launch {
            _mealsSearchTest.postValue(dummyList)
        }
    }


    /**Get Instant search items from Nutritionix api
     * Use SafeApicall to get result
     * post the result in _mealsSearch mutable live data
     * @param query
     */
    fun getInstantItems(query: String) {
        uiScope.launch {
            val data = afyaRepository.getInstantfood(query)
            when (data) {
                is NetworkResult.Success -> {
                    Timber.d("Data ${data}")
                    _mealsSearch.postValue(data)
                }
                is NetworkResult.Error -> {
                    Timber.d("Data error ${data.exception.message}")
                    _error.postValue("Error ")
                }
            }
        }
    }

    /**
     *
     * Get food items from internet via Coroutine backckground thread
     * and post the value to the _foodItem Mutablelivedata
     * this is observed by the fragment layout with the help of data binding binding adapters
     *
    *
     * @param query
     */
    fun getFoodItems(query: String) {
        uiScope.launch {
            val data = afyaRepository.getfoodItem(query)
            when (data) {
                is NetworkResult.Success -> {
                    Timber.d("Data ${data}")
                    _foodItem.postValue(data)
                }
                is NetworkResult.Error -> {
                    Timber.d("Data error ${data.exception.message}")
                    _error.postValue("Error ")
                }
            }
        }
    }

    /**
     * Clear the Coroutine Job
     *
     */
    override fun onCleared() {
        super.onCleared()
        uiScope.cancel()
    }

    /**
     * Factory for constructing AddMealViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddMealViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddMealViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}