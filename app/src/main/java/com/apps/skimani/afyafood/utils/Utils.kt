package com.apps.skimani.afyafood.utils

import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


import org.json.JSONObject

/**
 * Utility Class for this project
 *
 */
//class Utils {
//
//    companion object {
//        /**
//         * Converts a Json Object
//         * to a Request body with specified media Type
//         * @param requestJson
//         * @return
//         */
//        fun getRequestBody(requestJson: JSONObject): RequestBody {
//            return requestJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
//        }
//    }
//}
    class Utils {
        companion object{
            fun getRequestBody(requestJson: JSONObject): RequestBody {
                return requestJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
            }


            private const val PREFS_NAME = "afyaFood"
             const val PREFS_CALORIES_LIMIT = "calories_limit"

            /*** set shared preferences
             *  * @param con
             * @param key
             * @return
             */
            fun setPreference(con: Context, key: String?, value: String?) {
                val preferences = con.getSharedPreferences(PREFS_NAME, 0)
                val editor = preferences.edit()
                editor.putString(key, value)
                editor.apply()
            }

            /** get shared preferences
             * @param con
             * @param key
             * @return
             */
            fun getPreferences(con: Context, key: String?): String? {
                val sharedPreferences = con.getSharedPreferences(PREFS_NAME, 0)
                return sharedPreferences.getString(key, null)
            }


        }
    }