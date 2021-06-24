package com.apps.skimani.afyafood.utils


import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import com.google.android.material.chip.Chip
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

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
    companion object {
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
            return sharedPreferences.getString(key, "0")
        }


        fun pickDate(context: Context, chip: Chip) {
            val dateListener: DatePickerDialog.OnDateSetListener
            val myCalendar = Calendar.getInstance()
            val currYear = myCalendar[Calendar.YEAR]
            val currMonth = myCalendar[Calendar.MONTH]
            val currDay = myCalendar[Calendar.DAY_OF_MONTH]
            dateListener =
                DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = monthOfYear
                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    var isDateOk = true

                    val preferredFormat = "dd/MM/yyyy"
                    val date =
                        SimpleDateFormat(preferredFormat, Locale.ENGLISH).format(myCalendar.time)
                    chip.tag = formatDayDateTag(date)
                    val textString = formatRequestDate(date)
                    chip.text = textString
                    Timber.e("DATEE $date")
//                } else {
//                    binding.etDOB.error = "Age should be more than 18 years"
//                }
                }
            DatePickerDialog(
                context!!, dateListener, myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        fun formatRequestDate(date: String?): String {
            return when {
                date != null -> {
                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)//
                    try {
                        val dateFormat = format.parse(date)
                        val weekdayString =
                            SimpleDateFormat("EEE dd MMM/yyyy", Locale.getDefault()).format(
                                dateFormat
                            )
                        weekdayString.toString()
                    }catch (e:Exception){
                        ""
                    }
                }
                else -> {
                    ""
                }
            }
        }
        fun formatRequestTag(date: String?): String {
            return when {
                date != null -> {
                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)//
                    try {
                        val dateFormat = format.parse(date)
                        val weekdayString =
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                                dateFormat
                            )
                        weekdayString.toString()
                    }catch (e:Exception){
                        ""
                    }
                }
                else -> {
                    ""
                }
            }
        }


        fun formatDayDate(date: String?): String {
            return when {
                date != null -> {
                    val format = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)//
                    try {
                        val dateFormat = format.parse(date)
                        val weekdayString =
                            SimpleDateFormat("EEE dd MMM/yyyy", Locale.getDefault()).format(dateFormat)
                        weekdayString.toString()
                    }catch (e:Exception){
                        ""
                    }

                }
                else -> {
                    ""
                }
            }
        }

        fun formatDayDateTag(date: String?): String {
            return when {
                date != null -> {
                    val format = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)//
                    try {
                        val dateFormat = format.parse(date)
                        val weekdayString =
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dateFormat)
                        weekdayString.toString()
                    }catch (e:Exception){
                        ""
                    }

                }
                else -> {
                    ""
                }
            }
        }


        fun getAnyDay(day: Int,isTag:Boolean):String{
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, day)
            val date=cal.time.toString()
            Timber.d("YESTERDAY DATE ${formatDayDate(date)}")
            Timber.d("YESTERDAY TAG${formatDayDateTag(date)}")
            return if (isTag) {
                formatDayDateTag(date)
            }else
                formatDayDate(date)
        }
    }

}