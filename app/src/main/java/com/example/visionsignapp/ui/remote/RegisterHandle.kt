package com.example.visionsignapp.ui.remote

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun RegisterHandle(
    context: Context,
    fullName: MutableState<String>,
    email: MutableState<String>,
    password: MutableState<String>,
    gems: MutableState<Int>,
    level: MutableState<Int>,
    levelProgress: MutableState<Int>,
    icon: MutableState<String>,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    RetrofitInstance.getAuthApi()
        .signUp(User(fullName = fullName.value,email = email.value, password = password.value, gems = gems.value,level = level.value, levelProgress = levelProgress.value, icon = icon.value, categoriesProgress = null ))
        .enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful)
                    onSuccess()
                else
                    onFailure("Please check the form.")
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()

            }
        })
}