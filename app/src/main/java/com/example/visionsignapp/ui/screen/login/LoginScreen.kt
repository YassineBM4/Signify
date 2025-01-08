package com.example.visionsignapp.ui.screen.login

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.components.AuthentificationScreenTemplate
import com.example.visionsignapp.ui.theme.PrimaryPink
import com.example.visionsignapp.ui.theme.PrimaryPinkBlended
import com.example.visionsignapp.ui.theme.PrimaryPinkDark
import com.example.visionsignapp.ui.theme.PrimaryPinkLight
import com.example.visionsignapp.ui.theme.PrimaryViolet

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClicked: () -> Unit,
    onRegistrationClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    fullName: MutableState<String>,
    onCredentialsError: (String) -> Unit
) {
    // Get the current context
    val context = LocalContext.current

    // Handle back button press
    BackHandler {
        // Exit the app
        (context as? Activity)?.finishAffinity()
    }

    val emailError = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf("") }

    fun validateInputs(): Boolean {
        var isValid = true

        val emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        val emailTest = Regex(emailRegex)

        if (emailState.value.isEmpty()) {
            emailError.value = "Please enter your email."
            isValid = false
        } else if (!emailTest.matches(emailState.value)) {
            emailError.value = "Please enter a valid email address."
            isValid = false
        } else {
            emailError.value = ""
        }

        if (passwordState.value.isEmpty()) {
            passwordError.value = "Please enter your password."
            isValid = false
        } else if (passwordState.value.length < 8) {
            passwordError.value = "Password must be at least 8 characters."
            isValid = false
        } else if (!passwordState.value.any { it.isLetter() } || !passwordState.value.any { it.isDigit() }) {
            passwordError.value = "Password must contain both letters and numbers."
            isValid = false
        } else {
            passwordError.value = ""
        }

        return isValid
    }

    AuthentificationScreenTemplate(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryViolet.copy(alpha = 0.9f),
                        PrimaryViolet.copy(alpha = 0.3f)
                    )
                )
            ),
        imgRes = R.drawable.signify_logo,
        //title = "",
        //subtitle = "",
        mainActionButtonTile = "Login",
        secondaryActionButtonTitle = "Don't have an account? Create one.",
        mainActionButtonColors = ButtonDefaults.buttonColors(
            containerColor = PrimaryPinkDark,
            contentColor = Color.White
        ),
        secondaryActionButtonColors = ButtonDefaults.buttonColors(
            containerColor = PrimaryPinkLight,
            contentColor = Color.White
        ),
        actionButtonShadow = PrimaryPinkDark,
        onMainActionButtonClicked = {
            if (validateInputs()) {
                onLoginClicked()
            }
        },
        onSecondaryActionButtonClicked = onRegistrationClicked,
        onForgotPasswordClicked = onForgotPasswordClicked,
        emailState = emailState,
        passwordState = passwordState,
        fullName = fullName,
        emailError = emailError.value,
        passwordError = passwordError.value,
    )
}

