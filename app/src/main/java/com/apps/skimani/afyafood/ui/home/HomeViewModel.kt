package com.apps.skimani.afyafood.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apps.skimani.afyafood.database.Meal
import com.apps.skimani.afyafood.database.getDatabase
import com.apps.skimani.afyafood.repository.AfyaRepository
import kotlinx.coroutines.*
import timber.log.Timber

class HomeViewModel(app: Application) : ViewModel() {
    private var mealsJob = Job()
    private val uiScope = CoroutineScope(mealsJob + Dispatchers.IO)

    private val database = getDatabase(app)
    private val afyaRepository = AfyaRepository(database)

    var _allMeals = MutableLiveData<List<Meal>?>()
    val allMeals: LiveData<List<Meal>?>
        get() = _allMeals

    init {
        getAllMeals()
    }


    fun getAllMeals() {
        uiScope.launch {
            val data = afyaRepository.fetchAllMeals()
            Timber.d("ALL MEALS ${data}")
            _allMeals.postValue(data)
        }
    }

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