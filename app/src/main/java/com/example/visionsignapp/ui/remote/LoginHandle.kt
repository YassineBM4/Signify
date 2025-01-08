package com.example.visionsignapp.ui.remote

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

fun LoginHandle(
    context: Context,
    email: MutableState<String>,
    password: MutableState<String>,
    userPreferences: UserPreferences,
    coroutineScope: CoroutineScope,
    onSuccess: () -> Unit,
    onCredentialsError: (String) -> Unit
) {
    RetrofitInstance.getAuthApi()
        .login(LoginRequest(email = email.value, password = password.value))
        .enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    coroutineScope.launch {
                        userPreferences.saveToken(loginResponse.token)
                    }
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    onCredentialsError("Invalid credentials")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val errorMsg = t.message ?: "Network error"
                onCredentialsError(errorMsg)
            }
        })
}

