package com.example.visionsignapp.ui.remote

import android.util.Log

suspend fun updateCategoryLock(categoryId: String, isLockedStatus: Boolean, userPreferences: UserPreferences, callback: (Result<User>) -> Unit) {
    try {
        val authToken = userPreferences.getToken()
        if (authToken.isNullOrEmpty()) {
            callback(Result.failure(Exception("Missing auth token")))
            return
        }

        val response = RetrofitInstance.getAuthApi().updateCategoryLock(
            "Bearer $authToken",
            ChangeLockDto(categoryId = categoryId, isLockedStatus = isLockedStatus)
        )

        if (response.isSuccessful) {
            val updatedUser = response.body()
            if (updatedUser != null) {
                callback(Result.success(updatedUser))
            } else {
                callback(Result.failure(Exception("User data not found in response")))
            }
        } else {
            callback(Result.failure(Exception("Failed to update category lock")))
        }
    } catch (e: Exception) {
        callback(Result.failure(e))
        Log.e("API", "Error: ${e.message}")
    }
}

