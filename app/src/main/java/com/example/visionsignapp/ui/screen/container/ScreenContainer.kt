package com.example.visionsignapp.ui.screen.container

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.visionsignapp.ui.profile.ProfileScreen
import com.example.visionsignapp.ui.remote.LoginHandle
import com.example.visionsignapp.ui.remote.RegisterHandle
import com.example.visionsignapp.ui.remote.UserPreferences
import com.example.visionsignapp.ui.screen.VideoCall.LoginPage
import com.example.visionsignapp.ui.screen.content.ContentScreen
import com.example.visionsignapp.ui.screen.home.HomePage
import com.example.visionsignapp.ui.screen.lesson.LessonScreen
import com.example.visionsignapp.ui.screen.registration.RegistrationScreen
import com.example.visionsignapp.ui.screen.welcome.WelcomeScreen
import com.example.visionsignapp.ui.screen.login.ForgotPasswordScreen
import com.example.visionsignapp.ui.screen.login.VerificationCodeScreen
import com.example.visionsignapp.ui.screen.login.LoginScreen
import com.example.visionsignapp.ui.screen.login.ResetPasswordScreen
import com.example.visionsignapp.ui.screen.quizz.QuizzScreen
import com.example.visionsignapp.ui.screen.quizz.ResultScreen
import com.example.visionsignapp.ui.screen.shop.ShopScreen
import com.example.visionsignapp.ui.screen.welcome.OnboardingScreen

@OptIn(ExperimentalUnsignedTypes::class)
@Composable
fun ScreenContainer() {
    val coroutineScope = rememberCoroutineScope()
    val navHost = rememberNavController()
    val context = LocalContext.current
    val fullName = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val gemsState = remember { mutableStateOf(0) }
    val levelState = remember { mutableStateOf(0) }
    val levelProgressState = remember { mutableStateOf(0) }
    val iconState = remember { mutableStateOf("") }
    val showAlert = remember { mutableStateOf(false) }
    val alertMessage = remember { mutableStateOf("") }
    val userPreferences = remember { UserPreferences(context) }

    NavHost(navController =  navHost, startDestination = NavGraph.Welcome.route) {
        composable(NavGraph.Welcome.route) {
            WelcomeScreen(
                navHost,userPreferences,context
            )
        }
        composable(NavGraph.Onboarding.route) {
            OnboardingScreen(navHost, context)
        }
        composable(NavGraph.Login.route) {
            LoginScreen(
                onLoginClicked = {
                    LoginHandle(
                        context = context,
                        email = emailState,
                        password = passwordState,
                        userPreferences = userPreferences,
                        coroutineScope = coroutineScope,
                        onSuccess = {
                            navHost.navigate(NavGraph.Home.route)
                        },
                        onCredentialsError = { errorMessage ->
                            alertMessage.value = errorMessage
                            showAlert.value = true
                        }
                    )
                },
                onRegistrationClicked = {
                    navHost.navigate(NavGraph.Registration.route)
                },
                onForgotPasswordClicked = {
                    navHost.navigate(NavGraph.ForgotPassword.route)
                },
                fullName = fullName,
                emailState = emailState,
                passwordState = passwordState,
                onCredentialsError = { errorMessage ->
                    alertMessage.value = errorMessage
                    showAlert.value = true
                }
            )
        }
        composable(NavGraph.Registration.route) {
            RegistrationScreen(
                onRegistrationClicked = {
                    RegisterHandle(
                        context,
                        fullName,
                        emailState,
                        passwordState,
                        gemsState,
                        levelState,
                        levelProgressState,
                        iconState,
                        onSuccess = {
                            navHost.navigate(NavGraph.Login.route)
                        },
                        onFailure = { errorMessage ->
                            alertMessage.value = errorMessage
                            showAlert.value = true
                        }
                    )
                },
                onLoginClicked = {
                    navHost.navigate(NavGraph.Login.route)
                },
                fullName = fullName,
                emailState = emailState,
                passwordState = passwordState
            )
        }
        composable(NavGraph.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackLogin = {
                    navHost.navigate(NavGraph.Login.route)
                },
                onVerifyCode = { email ->
                    navHost.navigate(NavGraph.VerificationCodeScreen.createRoute(email))
                }
            )
        }
        composable(
            route = NavGraph.VerificationCodeScreen.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationCodeScreen(
                email = email,
                onVerifyCodeSuccess = {
                    navHost.navigate(NavGraph.ResetPassword.route)
                }
            )
        }
        composable(NavGraph.ResetPassword.route) {
            ResetPasswordScreen(
                navHost,
                onBackToLogin = {
                    navHost.navigate(NavGraph.Login.route)
                }
            )
        }
        composable(NavGraph.Profile.route) {
            ProfileScreen(userPreferences, navHost)
        }
        composable(NavGraph.Home.route) {
            HomePage(navHost,userPreferences)
        }
        composable(NavGraph.Lessons.route) {
            LessonScreen(navHost,userPreferences, context)
        }
        composable(route = NavGraph.Content.route) {
            ContentScreen(navHost)
        }
        composable(route = NavGraph.Quiz.route) {
            QuizzScreen(navHost, context)
        }
        composable(
            route = "result_screen/{correctAnswersCount}/{incorrectAnswers}/{expEarned}/{catProgress}/{categoryId}",
            arguments = listOf(
                navArgument("correctAnswersCount") { type = NavType.IntType },
                navArgument("incorrectAnswers") { type = NavType.IntType },
                navArgument("expEarned") { type = NavType.IntType },
                navArgument("catProgress") { type = NavType.IntType },
                navArgument("categoryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val correctAnswersCount = backStackEntry.arguments?.getInt("correctAnswersCount") ?: 0
            val incorrectAnswers = backStackEntry.arguments?.getInt("incorrectAnswers") ?: 0
            val expEarned = backStackEntry.arguments?.getInt("expEarned") ?: 0
            val catProgress = backStackEntry.arguments?.getInt("catProgress") ?: 0
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""

            ResultScreen(correctAnswersCount = correctAnswersCount, incorrectAnswers = incorrectAnswers, expEarned = expEarned, catProgress = catProgress, categoryId = categoryId, navHost)
        }
        composable(route = NavGraph.Shop.route) {
            ShopScreen(navHost, userPreferences)
        }
        composable(route = NavGraph.Room.route) {
            LoginPage(navHost, userPreferences)
        }
    }

    if (showAlert.value) {
        AlertDialog(
            onDismissRequest = { showAlert.value = false },
            title = { Text("Error") },
            text = { Text(alertMessage.value) },
            confirmButton = {
                Button(onClick = { showAlert.value = false }) {
                    Text("OK")
                }
            }
        )
    }
}
