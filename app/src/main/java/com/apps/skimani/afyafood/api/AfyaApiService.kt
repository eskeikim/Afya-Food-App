package com.apps.skimani.afyafood.api

import com.apps.skimani.afyafood.models.FoodResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AfyaApiService {
    /**
     * Fetches the searched food items from the api
     *
     * @param query
     * @return
     */
    @POST("natural/nutrients")
    suspend fun listFoodItem(@Body query: RequestBody): Response<FoodResponse>
 /**
     *Uses Autocomplete to search food item
     *
     * @param query
     * @return
     */
    @GET("search/instant")
    suspend fun fetchInstantItems(@Query("query") query: String): Response<FoodResponse>

}