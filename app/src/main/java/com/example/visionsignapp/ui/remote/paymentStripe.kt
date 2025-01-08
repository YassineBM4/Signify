package com.example.visionsignapp.ui.remote

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun startStripePayment(amount: Int, context: Context, onClientSecretReceived: (String) -> Unit) {
    val api = RetrofitInstance.getAuthApi()
    CoroutineScope(Dispatchers.IO).launch {
        val response = api.createPaymentIntent(PaymentIntentRequest(amount = amount * 100, currency = "usd"))
        if (response.isSuccessful) {
            val clientSecret = response.body()?.get("clientSecret") ?: ""
            withContext(Dispatchers.Main) {
                onClientSecretReceived(clientSecret)
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Payment initiation failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
