package com.sandbox.scopecodingchallenge.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.core.content.edit

class SharedPreferencesHelper {
    companion object {
        private const val KEY_SAVED_TIME = "saved_time"
        private var prefs: SharedPreferences? = null
        @Volatile
        private var instance: SharedPreferencesHelper? = null
        private val lock = Any()

        operator fun invoke(context: Context): SharedPreferencesHelper = instance ?: synchronized(lock) {
            instance ?: buildHelper(context).also {
                instance = it
            }
        }

        private fun buildHelper(context: Context): SharedPreferencesHelper {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesHelper()
        }
    }

    fun saveCacheDataTime() {
        prefs?.edit(commit = true) { putLong(KEY_SAVED_TIME, System.currentTimeMillis()) }
    }

    fun getCacheDataAge(): Long {
        val birth = prefs?.getLong(KEY_SAVED_TIME, 0) ?: 0
        return System.currentTimeMillis() - birth
    }
}