package com.example.visionsignapp.ui.remote

import kotlinx.coroutines.flow.firstOrNull

suspend fun fetchProfile(userPreferences: UserPreferences, onProfileLoaded: (User) -> Unit) {
    userPreferences.authToken.firstOrNull()?.let { token ->
        val api = RetrofitInstance.getAuthApi()
        val response = api.getProfile("Bearer $token")

        if (response.isSuccessful) {
            response.body()?.let { user ->
                println("User: $user")
                onProfileLoaded(user)
            }
        } else {
            println("Error fetching profile: ${response.errorBody()?.string()}")
        }
    }
}


