package com.example.visionsignapp.ui.remote

suspend fun resetPassword(
    email: String, newPassword: String
): Result<Unit> {
    return try {
        val response = RetrofitInstance.getAuthApi()
            .resetPassword(email, ResetPasswordDto(newPassword))
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Password reset failed: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}