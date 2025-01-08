package com.example.visionsignapp.ui.screen.welcome

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.remote.PreferencesManager
import com.example.visionsignapp.ui.remote.UserPreferences
import com.example.visionsignapp.ui.theme.PrimaryPink
import com.example.visionsignapp.ui.theme.PrimaryPinkBlended
import com.example.visionsignapp.ui.theme.PrimaryViolet

@Composable
fun WelcomeScreen(
    navController: NavController,
    userPreferences: UserPreferences,
    context: Context
) {
    val isLoading = remember { mutableStateOf(true) }
    val isLoggedIn by userPreferences.isLoggedIn.collectAsState(initial = false)
    //val isOnboardingCompleted = PreferencesManager.isOnboardingCompleted(context)


    LaunchedEffect(key1 = Unit) {
        kotlinx.coroutines.delay(3000)
        isLoading.value = false
        if (isLoggedIn) {
            navController.navigate("home_screen") {
                popUpTo("welcome_screen") { inclusive = true }
                launchSingleTop = true
}
        } else {
            navController.navigate("onboarding_screen") {
                popUpTo("login_screen") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryViolet.copy(alpha = 0.9f),
                        PrimaryViolet.copy(alpha = 0.3f)
                    )
                )
            )
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Center content vertically
    ) {
        // Centered Image
        Image(
            painter = painterResource(R.drawable.signify_logo),
            contentDescription = null,
            modifier = Modifier.size(350.dp) // Adjust size as needed
        )

        if (isLoading.value) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.padding(0.dp)
            )
        }
    }
}