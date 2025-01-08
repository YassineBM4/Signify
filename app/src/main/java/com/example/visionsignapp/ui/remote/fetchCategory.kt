package com.example.visionsignapp.ui.remote

suspend fun fetchCategories(onCategoriesLoaded: (List<Category>) -> Unit) {
    val api = RetrofitInstance.getAuthApi()
    val response = api.getCategory()
    if (response.isSuccessful) {
        response.body()?.let { categoriesResponse ->
            onCategoriesLoaded(categoriesResponse.categories)
            println("Categories: ${categoriesResponse.categories}")
        }
    } else {
        println("Error fetching categories: ${response.errorBody()?.string()}")
    }
}
