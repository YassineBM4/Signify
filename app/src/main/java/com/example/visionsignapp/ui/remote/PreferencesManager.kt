package com.example.visionsignapp.ui.remote

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREF_NAME = "vision_sign_app"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

    fun setOnboardingCompleted(context: Context, completed: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun isOnboardingCompleted(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
}
