package com.example.healthtrack.core.notification

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReminderPreferences {
    private const val PREF_NAME = "salud_integral_reminder_prefs"
    private const val KEY_LAST_RECORDED_DATE = "last_recorded_date"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun markMeasurementRecordedToday(context: Context) {
        getPrefs(context).edit()
            .putString(KEY_LAST_RECORDED_DATE, getTodayDateString())
            .apply()
    }

    fun hasRecordedToday(context: Context): Boolean {
        val lastDate = getPrefs(context).getString(KEY_LAST_RECORDED_DATE, null)
        return lastDate == getTodayDateString()
    }
}
