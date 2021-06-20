package com.apps.skimani.afyafood.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


import org.json.JSONObject

/**
 * Utility Class for this project
 *
 */
class Utils {
    companion object{
        /**
         * Converts a Json Object
         * to a Request body with specified media Type
         * @param requestJson
         * @return
         */
        fun getRequestBody(requestJson: JSONObject): RequestBody {
            return requestJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
        }
    }
}