package com.apps.skimani.afyafood.ui.preference

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apps.skimani.afyafood.ui.home.HomeViewModel
import com.apps.skimani.afyafood.utils.Utils

class PreferenceViewModel(app:Application) : ViewModel() {

    private val _dailyCaloriesLimit = MutableLiveData<String>()
    val dailyCaloriesLimit: LiveData<String>
    get() = _dailyCaloriesLimit

    init {
        _dailyCaloriesLimit.value=Utils.getPreferences(app.applicationContext,Utils.PREFS_CALORIES_LIMIT)
    }

    /**
     * Factory for constructing PreferenceViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PreferenceViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PreferenceViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}