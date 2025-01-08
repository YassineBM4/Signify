package com.example.visionsignapp.ui.remote

import androidx.compose.runtime.MutableState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

 fun sendPasswordResetEmail(
     email: MutableState<String>,
     onResult: (Boolean) -> Unit
 ) {
     RetrofitInstance.getAuthApi()
         .sendPasswordResetEmail(PasswordResetRequest(email = email.value))
         .enqueue(object : Callback<Void> {
             override fun onResponse(call: Call<Void>, response: Response<Void>) {
                 onResult(response.isSuccessful)
             }
             override fun onFailure(call: Call<Void>, t: Throwable) {
                 onResult(false)
             }
         })
}