package com.example.visionsignapp.ui.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

fun fetchAndVerifyCode(
    api: AuthApi,
    email: String,
    resetCode: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val verifyOtpDto = VerifyOtpDto(email, resetCode)

    api.verifyResetCode(verifyOtpDto).enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                onSuccess()
            } else {
                onFailure("Invalid or expired verification code.")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            onFailure("An error occurred: ${t.localizedMessage}")
            Log.e("VerificationError", "Error verifying code", t)
        }
    })
}
