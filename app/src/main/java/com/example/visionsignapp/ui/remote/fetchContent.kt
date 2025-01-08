package com.example.visionsignapp.ui.remote

suspend fun fetchContents(categoryId: String, onContentsLoaded: (List<Content>) -> Unit) {
    val api = RetrofitInstance.getAuthApi()
    val response = api.getContents(categoryId)

    if (response.isSuccessful) {
        response.body()?.let { contentsResponse ->
            onContentsLoaded(contentsResponse.contents)
        }
    } else {
        println("Error fetching contents: ${response.errorBody()?.string()}")
    }
}