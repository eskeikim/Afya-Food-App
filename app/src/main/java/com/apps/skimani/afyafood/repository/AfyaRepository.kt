package com.apps.skimani.afyafood.repository

import com.apps.skimani.afyafood.api.RestClient
import com.apps.skimani.afyafood.models.InstantFoodItemResponse
import com.apps.skimani.afyafood.utils.safeApiCall
import com.apps.skimani.foodie.utils.NetworkResult
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

class AfyaRepository {
suspend fun getInstantfood(query: String) = safeApiCall(
     call = {getFooItems(query) },
    errorMessage= "Error occurred"
)

    private suspend fun getFooItems(query:String): NetworkResult<InstantFoodItemResponse> {
    val response=RestClient.apiService.fetchInstantItems(query)
        return when {
            response.isSuccessful->{
                Timber.d("repo Data ${response.body()}")
                NetworkResult.Success(response.body()!! )
            }
            else->{
                Timber.d("repo error ${response.code()}")
                NetworkResult.Error(IOException("Error fetching the food"))
            }
        }
    }

}