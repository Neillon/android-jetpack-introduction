package com.neillon.dogs.common.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class SharedPreferencesHelper {

    companion object {
        private const val PREFERENCE_TIME = "preference_time"

        private var preferences: SharedPreferences? = null
        @Volatile private var instance: SharedPreferencesHelper? = null
        private val LOCK = Any()

        operator fun  invoke(context: Context): SharedPreferencesHelper = instance ?: synchronized(LOCK) {
            instance ?: buildHelper(context).also {
                instance = it
            }
        }

        private fun buildHelper(context: Context): SharedPreferencesHelper {
            preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesHelper()
        }
    }

    fun saveUpdateTime(time: Long) {
        preferences?.edit(commit = true) {
            putLong(PREFERENCE_TIME, time)
        }
    }

    fun getUpdateTime() = preferences?.getLong(PREFERENCE_TIME, 0)

    fun getCacheDuration() = preferences?.getString("pref_cache_duration", "")
}