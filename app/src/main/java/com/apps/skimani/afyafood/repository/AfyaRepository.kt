package com.apps.skimani.afyafood.repository

import com.apps.skimani.afyafood.api.RestClient
import com.apps.skimani.afyafood.models.FoodResponse
import com.apps.skimani.afyafood.models.InstantFoodItemResponse
import com.apps.skimani.afyafood.utils.safeApiCall
import com.apps.skimani.foodie.utils.NetworkResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

class AfyaRepository {
suspend fun getInstantfood(query: String) = safeApiCall(
     call = {getInstanttems(query) },
    errorMessage= "Error occurred"
)

    private suspend fun getInstanttems(query:String): NetworkResult<InstantFoodItemResponse> {
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
suspend fun getfoodItem(query: String) = safeApiCall(
     call = {getFoodtems(query) },
    errorMessage= "Error occurred"
)

    private suspend fun getFoodtems(query:String): NetworkResult<FoodResponse> {
        val jsObj= JSONObject()
        jsObj.put("query",query)
    val response=RestClient.apiService.listFoodItem(getRequestBody(jsObj))
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
    fun getRequestBody(requestJson: JSONObject): RequestBody {
        return requestJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }

}