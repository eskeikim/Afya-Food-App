package com.apps.skimani.afyafood.utils

import android.util.Log
import com.apps.skimani.foodie.utils.NetworkResult
import retrofit2.HttpException
import java.io.IOException

suspend fun <T : Any> safeApiCall(
    call: suspend () -> NetworkResult<T>,
    errorMessage: String
): NetworkResult<T> =
    try {
        call.invoke()
    } catch (e: Throwable) {
        NetworkResult.Error(IOException(errorMessage, e))
    }