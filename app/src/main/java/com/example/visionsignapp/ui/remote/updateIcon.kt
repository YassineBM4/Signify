package com.example.visionsignapp.ui.remote

suspend fun updateUserIcon(updatedIcon: String, authToken: String): Result<User> {
    return try {
        val response = RetrofitInstance.getAuthApi().updateIcon(
            "Bearer $authToken",
            editIconDto = EditIconDto(updatedIcon)
        )

        if (response.isSuccessful) {
            val updatedIcon = response.body()
            if (updatedIcon != null) {
                Result.success(updatedIcon)
            } else {
                Result.failure(Exception("Icon not found in response"))
            }
        } else {
            Result.failure(Exception("Failed to update icon"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}