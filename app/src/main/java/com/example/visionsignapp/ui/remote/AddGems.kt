package com.example.visionsignapp.ui.remote

import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response

suspend fun buyGems(userPreferences: UserPreferences, earnedGems: Int): Result<AwardGemsResponse> {
    val token = userPreferences.authToken.firstOrNull()
    if (token == null) {
        return Result.failure(Throwable("Auth token not found"))
    }
    val api = RetrofitInstance.getAuthApi()
    val addGemsDto = AddGemsDto(earnedGems)

    try {
        val response: Response<AwardGemsResponse> = api.buyGems("Bearer $token", addGemsDto)

        if (response.isSuccessful) {
            return Result.success(response.body()!!)
        } else {
            return Result.failure(Throwable("Error awarding gems: ${response.message()}"))
        }
    } catch (e: Exception) {
        return Result.failure(Throwable("Exception while awarding gems: ${e.localizedMessage}"))
    }
}