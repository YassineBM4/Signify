package com.example.visionsignapp.ui.remote

import retrofit2.Response

suspend fun fetchShop(onShopLoaded: (List<Shop>?) -> Unit) {
    val api = RetrofitInstance.getAuthApi()
    val response: Response<List<Shop>> = api.fetchShop()

    if (response.isSuccessful) {
        response.body()?.let { shopList ->
            onShopLoaded(shopList)
        }
    } else {
        println("Error fetching shop: ${response.errorBody()?.string()}")
        onShopLoaded(null)
    }
}
