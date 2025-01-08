package com.example.visionsignapp.ui.remote

import android.util.Log

suspend fun fetchQuizzes(categoryId: String, onResult: (List<Quizz>) -> Unit) {
    val api = RetrofitInstance.getAuthApi()
    val response = api.getQuizzes(categoryId)
    Log.d("API Response", "Status Code: ${response.code()}")
    if (response.isSuccessful) {
        val body = response.body()
        Log.d("API Response", "Fetched body: $body")
        val quizzes = body?.quizzes ?: emptyList()
        Log.d("API Response", "Fetched quizzes: $quizzes")
        onResult(quizzes)
    } else {
        Log.e("API Error", "Error: ${response.message()}")
        onResult(emptyList())
    }
}
