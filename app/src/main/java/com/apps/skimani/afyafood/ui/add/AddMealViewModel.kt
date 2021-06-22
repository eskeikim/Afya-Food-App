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
import com.apps.skimani.afyafood.ui.home.HomeViewModel
import com.apps.skimani.foodie.utils.NetworkResult
import kotlinx.coroutines.*
import org.json.JSONObject
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


    var _foodItemTempValue = MutableLiveData<List<FoodItem>?>()
    val foodItemTempValue: LiveData<List<FoodItem>?>
        get() = _foodItemTempValue

    private var _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private var mealsJob = Job()
    private val uiScope = CoroutineScope(mealsJob + Dispatchers.IO)
    val dummyList = ArrayList<BrandedList>()
    private val database = getDatabase(app)
    private val afyaRepository = AfyaRepository(database)

    init {
        dummyList.add(
            BrandedList(
                "Gither", "Githe", 3, "Kenya Githeri", "Kenya", 44,
                "23", "32", BrandedPhoto(
                    "",
                    false, "https://nix-tag-images.s3.amazonaws.com/3917_thumb.jpg"
                ), 33, 23, ""
            ),
        )
        dummyList.add(
            BrandedList(
                "Rice", "Rice", 3, "Kenya Rice", "Kenya", 76,
                "23", "32", BrandedPhoto(
                    "",
                    false, "https://nix-tag-images.s3.amazonaws.com/3917_thumb.jpg"
                ), 33, 4, ""
            ),
        )
        getInstantItemsTest("")
        getFoodItemRoomDB()
    }

    val tempFoodItems = afyaRepository.foodItems


    //    lateinit var tempFoodItems:List<FoodItem?>
    fun getFoodItemRoomDB() {
        uiScope.launch {
            val value = afyaRepository.fetchValue()
//            Timber.e("VIEWMODEL ${value?.get(1)?.foodName}")
            _foodItemTempValue.postValue(value)
//        tempFoodItems=afyaRepository.foodItems.value!!
        }
    }

    fun saveFoodItemRoomDB(foodItem: FoodItem) {
        uiScope.launch {
            afyaRepository.saveFoodItem(foodItem)
        }
    }

    fun saveAMealRoomDB(meal: Meal) {
        uiScope.launch {
            afyaRepository.saveAMeal(meal)
        }
    }

    fun getInstantItemsTest(query: String) {
        uiScope.launch {
            _mealsSearchTest.postValue(dummyList)
        }
    }



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