package com.example.visionsignapp.ui.screen.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.components.AuthentificationScreenTemplate
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.PrimaryVioletLight

@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    onRegistrationClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    fullName: MutableState<String>,
    emailState: MutableState<String>,
    passwordState: MutableState<String>
) {
    val fullNameError = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    fun validateInputs(): Boolean {
        var isValid = true
        val errors = mutableListOf<String>()

        if (fullName.value.isEmpty()) {
            fullNameError.value = "Please enter your full name."
            errors.add("Full name is required.")
            isValid = false
        } else if (fullName.value.length < 2) {
            fullNameError.value = "Full name must be at least 2 characters."
            errors.add("Full name must be at least 2 characters.")
            isValid = false
        } else {
            fullNameError.value = ""
        }

        val emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        val emailTest = Regex(emailRegex)

        if (emailState.value.isEmpty()) {
            emailError.value = "Please enter your email."
            errors.add("Email is required.")
            isValid = false
        } else if (!emailTest.matches(emailState.value)) {
            emailError.value = "Please enter a valid email address."
            errors.add("Please enter a valid email address.")
            isValid = false
        } else {
            emailError.value = ""
        }

        if (passwordState.value.isEmpty()) {
            passwordError.value = "Please enter your password."
            errors.add("Password is required.")
            isValid = false
        } else if (passwordState.value.length < 8) {
            passwordError.value = "Password must be at least 8 characters."
            errors.add("Password must be at least 8 characters.")
            isValid = false
        } else if (!passwordState.value.any { it.isLetter() } || !passwordState.value.any { it.isDigit() }) {
            passwordError.value = "Password must contain both letters and numbers."
            errors.add("Password must contain both letters and numbers.")
            isValid = false
        } else if (!passwordState.value.any { it.isUpperCase() }) {
            passwordError.value = "Password must contain at least one uppercase letter."
            errors.add("Password must contain at least one uppercase letter.")
            isValid = false
        } else {
            passwordError.value = ""
        }

        if (errors.isNotEmpty()) {
            dialogMessage.value = errors.joinToString("\n")
            showDialog.value = true
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
        mainActionButtonTile = "Create an Account",
        secondaryActionButtonTitle = "Already have an account? Login.",
        mainActionButtonColors = ButtonDefaults.buttonColors(
            containerColor = PrimaryVioletDark,
            contentColor = Color.White
        ),
        secondaryActionButtonColors = ButtonDefaults.buttonColors(
            containerColor = PrimaryVioletLight,
            contentColor = Color.White
        ),
        actionButtonShadow = PrimaryVioletDark,
        onMainActionButtonClicked = {
            if (validateInputs()) {
                onRegistrationClicked()
            }
        },
        onSecondaryActionButtonClicked = onLoginClicked,
        onForgotPasswordClicked = {},
        showForgotPassword = false,
        showNameField = true,
        fullName = fullName,
        emailState = emailState,
        passwordState = passwordState,
        fullNameError = fullNameError.value,
        emailError = emailError.value,
        passwordError = passwordError.value
    )

    if (showDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                androidx.compose.material3.Button(onClick = { showDialog.value = false }) {
                    androidx.compose.material3.Text("OK")
                }
            },
            title = { androidx.compose.material3.Text("Validation Errors") },
            text = { androidx.compose.material3.Text(dialogMessage.value) }
        )
    }
}
