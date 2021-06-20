package com.apps.skimani.afyafood.ui.add

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apps.skimani.afyafood.models.InstantFoodItemResponse
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
    private var _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private var mealsJob= Job()
    private val uiScope= CoroutineScope(mealsJob + Dispatchers.IO)
    val afyaRepository=AfyaRepository()

    init {

    }

   fun getFoodItems(query: String){
       uiScope.launch {
           val data=afyaRepository.getInstantfood(query)
           when(data){
               is NetworkResult.Success->{
                   Timber.d("Data ${data}")
                   _mealsSearch.postValue(data)
               }
               is NetworkResult.Error->{
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