package com.example.visionsignapp.ui.remote

suspend fun updateUserPassword(oldPassword: String, updatedPassword: String, authToken: String): Result<User> {
    return try {
        val response = RetrofitInstance.getAuthApi().changePassword(
            "Bearer $authToken",
            changePasswordDto = ChangePasswordDto(oldPassword , updatedPassword)
        )

        if (response.isSuccessful) {
            val updatedPass = response.body()
            if (updatedPass != null) {
                Result.success(updatedPass)
            } else {
                Result.failure(Exception("User not found in response"))
            }
        } else {
            Result.failure(Exception("Failed to update password"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}