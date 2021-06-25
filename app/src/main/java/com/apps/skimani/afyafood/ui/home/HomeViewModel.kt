package com.apps.skimani.afyafood.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apps.skimani.afyafood.database.Meal
import com.apps.skimani.afyafood.database.getDatabase
import com.apps.skimani.afyafood.repository.AfyaRepository
import com.apps.skimani.afyafood.utils.Utils
import kotlinx.coroutines.*
import timber.log.Timber

class HomeViewModel(app: Application) : ViewModel() {
    private var mealsJob = Job()
    private val uiScope = CoroutineScope(mealsJob + Dispatchers.IO)

    private val database = getDatabase(app)
    private val afyaRepository = AfyaRepository(database)

    private var _allMeals = MutableLiveData<List<Meal>?>()
    val allMeals: LiveData<List<Meal>?>
        get() = _allMeals
    private var _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    private var _mealsByDay = MutableLiveData<List<Meal>?>()
    val mealsByDay: LiveData<List<Meal>?>
        get() = _mealsByDay

    private val _dailyCaloriesLimit = MutableLiveData<String>()
    val dailyCaloriesLimit: LiveData<String>
        get() = _dailyCaloriesLimit

    init {
        _dailyCaloriesLimit.value= Utils.getPreferences(app.applicationContext, Utils.PREFS_CALORIES_LIMIT)
        _isEmpty.value=false
//        getAllMeals()
    }

    /**
     * Get saved meals by the selected day via a background thread
     * and post the value to the _mealsByDay Mutablelivedata
     * this is observed by the homefragment layout with the help of data binding binding adapters
     *Any error is observed and binded to the layout file
     * @param query
     */
    fun getMealsByDay(query:String) {
        uiScope.launch {
            val data = afyaRepository.fetchMealsByDay(query)
            Timber.d("DAY MEALS ${data?.size}")
            if (data?.size!! >0){
                _mealsByDay.postValue(data)
                _isEmpty.postValue(false)

                if (data != null) {
                for (item in data) {
                    Timber.d("Item Name ${item.name} >> ${item.totalCalories}")
                }
            }
            }else{
             _isEmpty.postValue(true)
            }
        }
    }
    /**
     * Get All saved meals
     *
     */
    fun getAllMeals() {
        uiScope.launch {
            val data = afyaRepository.fetchAllMeals()
            Timber.d("ALL MEALS ${data?.size}")
            if (data != null) {
                for (item in data){
                    Timber.d("Item Name ${item.name} >> ${item.totalCalories}")
                }
            }
            _allMeals.postValue(data)
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
     * Factory for constructing HomeViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}