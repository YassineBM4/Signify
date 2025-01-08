package com.example.visionsignapp.ui.screen.shop

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.components.CustomLevelBar
import com.example.visionsignapp.ui.remote.Category
import com.example.visionsignapp.ui.remote.Shop
import com.example.visionsignapp.ui.remote.UserPreferences
import com.example.visionsignapp.ui.remote.buyGems
import com.example.visionsignapp.ui.remote.fetchProfile
import com.example.visionsignapp.ui.remote.fetchShop
import com.example.visionsignapp.ui.remote.startStripePayment
import com.example.visionsignapp.ui.screen.home.BottomNavigationBar
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.PrimaryVioletShop
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

@Composable
fun ShopScreen(navController: NavController, userPreferences: UserPreferences) {
    var fullName by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(0) }
    var gems by remember { mutableStateOf(0) }
    val context = LocalContext.current
    var shopList by remember { mutableStateOf<List<Shop>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val iconSize = screenWidth * 0.15f
    val fontSize = screenWidth * 0.05f
    val coinSize = screenWidth * 0.12f
    val textPadding = screenWidth * 0.02f

    // Fetch the shop data from the API
    LaunchedEffect(Unit) {
        fetchShop { fetchedShop ->
            if (fetchedShop != null) {
                shopList = fetchedShop
            } else {
                errorMessage = "Error fetching shop"
            }
        }
    }

    // Fetch user profile data
    LaunchedEffect(Unit) {
        fetchProfile(userPreferences) { user ->
            fullName = user.fullName ?: "Full name not available"
            icon = user.icon ?: ""
            level = user.level ?: 0
            gems = user.gems ?: 0
        }
    }

    // Root Box to stack background and foreground
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.shopify),
            contentDescription = "Shop Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(PrimaryVioletShop.value).copy(alpha = 0.4f), shape = RoundedCornerShape(20.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val imageResId = context.resources.getIdentifier(
                                icon,
                                "drawable",
                                context.packageName
                            )
                            Image(
                                painter = painterResource(id = if (imageResId != 0) imageResId else R.drawable.profile_icon),
                                contentDescription = "Profile Icon",
                                modifier = Modifier.size(iconSize)
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 10.dp)
                            ) {
                                androidx.compose.material3.Text(
                                    text = "$fullName ~ lvl.$level",
                                    fontSize = fontSize.value.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(start = textPadding, top = textPadding)
                                )
                                CustomLevelBar(userPreferences)
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    navController.navigate("shop_screen")
                                }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.signify_coin),
                                    contentDescription = "Signify Coin",
                                    modifier = Modifier
                                        .size(coinSize)
                                        .clip(CircleShape)
                                        .background(Color.Transparent)
                                        .padding(top = textPadding, end = textPadding)
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                val animatedGems by animateIntAsState(
                                    targetValue = gems,
                                    animationSpec = tween(durationMillis = 500)
                                )
                                androidx.compose.material3.Text(
                                    text = "$animatedGems SC",
                                    fontSize = (fontSize.value * 0.9f).sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 16.dp),
                            color = Color.LightGray
                        )
                    }
                    /*Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(PrimaryVioletShop.value).copy(alpha = 0.4f), shape = RoundedCornerShape(20.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val imageResId = context.resources.getIdentifier(
                            icon,
                            "drawable",
                            context.packageName
                        )
                        if (imageResId != 0) {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = "Profile Icon",
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color.Transparent)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.profile_icon),
                                contentDescription = "Profile Icon",
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color.Transparent)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                                .padding(12.dp)
                                .offset(x = -10.dp, y = -5.dp)
                        ) {
                            Column {
                                Text(
                                    text = "$fullName ~ lvl.$level",
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                                )
                                CustomLevelBar(userPreferences)
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("shop_screen")
                                }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.signify_coin),
                                contentDescription = "Signify Coin",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                                    .padding(top = 10.dp, end = 5.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            val animatedGems by animateIntAsState(
                                targetValue = gems,
                                animationSpec = tween(durationMillis = 500)
                            )
                            Text(
                                text = "$animatedGems SC",
                                fontSize = 18.sp,
                                color = Color.White,
                            )
                        }
                    }*/
                }

                // Shop List Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .offset(y = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        items(shopList) { shop ->
                            ShopItemCard(userPreferences, navController, shop)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShopItemCard(userPreferences: UserPreferences, navController: NavController, shop: Shop) {
    val context = LocalContext.current
    var clientSecret by remember { mutableStateOf<String?>(null) }
    var showPaymentScreen by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf<Boolean?>(null) }

    val buyGems = {
        isLoading = true
        CoroutineScope(Dispatchers.Main).launch {
            val result = shop.scPrice?.let { buyGems(userPreferences, it) }
            isLoading = false
            if (result != null) {
                result.onSuccess {
                    isSuccess = true
                }.onFailure {
                    isSuccess = false
                }
            }
        }
    }

    if (showPaymentScreen && clientSecret != null) {
        ModalBottomSheet(
            onDismissRequest = { showPaymentScreen = false },
            containerColor = PrimaryVioletLittleDarker,
            contentColor = Color.White,
            modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
        ) {
            PaymentScreen(
                shop,
                clientSecret = clientSecret!!,
                onPaymentComplete = { success ->
                    buyGems()
                    navController.navigate("shop_screen")
                    showPaymentScreen = false
                    if (success) {
                        Toast.makeText(context, "Payment successful!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Payment failed. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(PrimaryVioletLittleDarker.value))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display Shop Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(PrimaryVioletLittleDarker.value)),
                    contentAlignment = Alignment.Center
                ) {
                    val imageResId = context.resources.getIdentifier(
                        shop.scImage,
                        "drawable",
                        context.packageName
                    )
                    Image(
                        painter = painterResource(
                            id = if (imageResId != 0) imageResId else R.drawable.profile_icon
                        ),
                        contentDescription = "Shop Image",
                        modifier = Modifier.size(170.dp).background(Color.Transparent)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${shop.scPrice} Signify Coin",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = {
                        startStripePayment(shop.realPrice ?: 0, context) { secret ->
                            clientSecret = secret
                            showPaymentScreen = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    colors = ButtonDefaults.buttonColors(Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${shop.realPrice} USD",
                            color = Color(PrimaryVioletDark.value),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
}

@SuppressLint("RestrictedApi")
@Composable
fun PaymentScreen(shop: Shop, clientSecret: String, onPaymentComplete: (Boolean) -> Unit) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var selectedCardType by remember { mutableStateOf("Visa") }
    val context = LocalContext.current
    val stripe = Stripe(context, "pk_test_51QcRZmDQ71d8FZXBTI2suFLan5CRrIRd67HyqpnSEnJ01mEs8srCG6xgQfB5Ub3H6RmlgQ30JjlXxoFvenxvTNaR005BeXJEt7")
    val paymentIntentId = clientSecret.split("_secret_")[0]
    var isDialogConfirmed by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    if(showDialog == true) {
        UnlockDialog(
            shop,
            onConfirm = {
                isDialogConfirmed = true
            },
            onDismiss = {
                isDialogConfirmed = false
            }
        )
    }

    fun confirmPayment(paymentMethodId: String) {
        val requestBody = mapOf(
            "payment_method" to paymentMethodId
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                val stripeApiUrl = "https://api.stripe.com/v1/payment_intents/$paymentIntentId/confirm"
                val stripeSecretKey = "sk_test_51QcRZmDQ71d8FZXBwmjYCMBIZxPVH0NY3jRo8MqnkkHHeTQMdzCWQwaNlKR7Qhiqd8e9QOyIMbEblDu6WPagj1zo00xdaFOmzu"

                val request = Request.Builder()
                    .url(stripeApiUrl)
                    .post(RequestBody.create("application/x-www-form-urlencoded".toMediaTypeOrNull(), requestBody.entries.joinToString("&") { "${it.key}=${it.value}" }))
                    .addHeader("Authorization", "Bearer $stripeSecretKey")
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val paymentIntent = JSONObject(responseBody)
                    val status = paymentIntent.optString("status")
                    if (status == "succeeded") {
                        withContext(Dispatchers.Main) {
                            println("Payment intent succeeded")
                            onPaymentComplete(true)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            println("Payment failed or pending: $status")
                            onPaymentComplete(false)
                        }
                    }
                } else {
                    val errorBody = response.body?.string()
                    println("Payment failed. Error body: $errorBody")
                    withContext(Dispatchers.Main) {
                        onPaymentComplete(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Error confirming payment: ${e.localizedMessage}")
                    onPaymentComplete(false)
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Image(
                painter = painterResource(id = R.drawable.visa),
                contentDescription = "Visa Logo",
                modifier = Modifier.size(65.dp)
            )
            RadioButton(
                selected = selectedCardType == "Visa",
                onClick = { selectedCardType = "Visa" }
            )
            Text("Visa", modifier = Modifier.padding(start = 3.dp))

            Spacer(modifier = Modifier.width(16.dp))

            Image(
                painter = painterResource(id = R.drawable.mc),
                contentDescription = "Mastercard Logo",
                modifier = Modifier.size(50.dp)
            )

            RadioButton(
                selected = selectedCardType == "Mastercard",
                onClick = { selectedCardType = "Mastercard" }
            )
            Text("Mastercard", modifier = Modifier.padding(start = 3.dp))
        }

        TextField(
            value = cardNumber,
            onValueChange = { newCardNumber ->
                if (newCardNumber.length <= 16 && newCardNumber.all { it.isDigit() }) {
                    cardNumber = newCardNumber
                }
            },
            label = { Text("Card Number", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = expiryDate,
            onValueChange = { newExpiryDate ->
                if (newExpiryDate.length <= 5 && newExpiryDate.all { it.isDigit() || it == '/' }) {
                    expiryDate = newExpiryDate
                }
            },
            label = { Text("Expiry Date (MM/YY)", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = cvc,
            onValueChange = { newCvc ->
                if (newCvc.length <= 3 && newCvc.all { it.isDigit() }) {
                    cvc = newCvc
                }
            },
            label = { Text("CVC", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.height(50.dp).fillMaxWidth(),
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(PrimaryVioletDark.value),
            )
        ) {
            Text("Confirm Payment", color = Color.White, fontSize = 17.sp)
        }
        if (showDialog) {
            UnlockDialog(
                shop = shop,
                onConfirm = {
                    showDialog = false
                    val expiryMonth = expiryDate.split("/")[0].toIntOrNull()
                    val expiryYear = expiryDate.split("/")[1].toIntOrNull()?.plus(2000)
                    val cardParams = PaymentMethodCreateParams.Card(
                        number = cardNumber,
                        expiryMonth = expiryMonth,
                        expiryYear = expiryYear,
                        cvc = cvc
                    )
                    val params = PaymentMethodCreateParams.create(cardParams, null)
                    if (params != null) {
                        val confirmParams =
                            ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                                params,
                                clientSecret
                            )
                        val paymentMethodId = when (selectedCardType) {
                            "Visa" -> "pm_card_visa"
                            "Mastercard" -> "pm_card_mastercard"
                            else -> ""
                        }

                        if (paymentMethodId.isNotEmpty()) {
                            confirmPayment(paymentMethodId)
                        } else {
                            onPaymentComplete(false)
                        }
                    } else {
                        onPaymentComplete(false)
                    }
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun UnlockDialog(shop: Shop, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Buy Signify Coins") },
        text = {
            Text(
                "You are about to pay ${shop.realPrice}$. Do you Confirm your payment?"
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}