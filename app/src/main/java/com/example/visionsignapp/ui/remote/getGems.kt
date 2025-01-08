package com.example.visionsignapp.ui.repository

import android.util.Log
import com.example.visionsignapp.ui.remote.AwardGemsDto
import com.example.visionsignapp.ui.remote.AwardGemsResponse
import com.example.visionsignapp.ui.remote.RetrofitInstance
import com.example.visionsignapp.ui.remote.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response

suspend fun awardGems(userPreferences: UserPreferences, correctAnswersCount: Int, expEarned: Int, catProgress: Int, categoryId: String): Result<AwardGemsResponse> {
    val token = userPreferences.authToken.firstOrNull()
    if (token == null) {
        return Result.failure(Throwable("Auth token not found"))
    }

    val api = RetrofitInstance.getAuthApi()
    val awardGemsDto = AwardGemsDto(correctAnswersCount, expEarned, catProgress, categoryId)

    try {
        val response: Response<AwardGemsResponse> = api.awardGems("Bearer $token", awardGemsDto)

        if (response.isSuccessful) {
            return Result.success(response.body()!!)
        } else {
            return Result.failure(Throwable("Error awarding gems: ${response.message()}"))
        }
    } catch (e: Exception) {
        return Result.failure(Throwable("Exception while awarding gems: ${e.localizedMessage}"))
    }
}