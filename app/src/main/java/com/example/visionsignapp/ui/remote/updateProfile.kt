package com.example.visionsignapp.ui.remote

suspend fun updateUserProfile(updatedName: String, updatedEmail: String, authToken: String): Result<User> {
    return try {
        val response = RetrofitInstance.getAuthApi().updateProfile(
            "Bearer $authToken",
            editProfileDto = EditProfileDto(updatedName, updatedEmail)
        )

        if (response.isSuccessful) {
            val updatedUser = response.body()
            if (updatedUser != null) {
                Result.success(updatedUser)
            } else {
                Result.failure(Exception("User not found in response"))
            }
        } else {
            Result.failure(Exception("Failed to update profile"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}