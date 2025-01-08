package com.example.visionsignapp.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.remote.sendPasswordResetEmail
import com.example.visionsignapp.ui.theme.*
import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

var EMAIL = ""

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackLogin: () -> Unit,
    onVerifyCode: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var showAlert by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showEmptyEmailError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    EMAIL = email

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FORGOT YOUR PASSWORD?",
            color = PrimaryViolet,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            fontWeight = FontWeight.Bold,
            fontSize = (screenWidth*0.08f).value.sp
        )

        Image(
            painter = painterResource(id = R.drawable.pass),
            contentDescription = null,
            modifier = Modifier
                .size(screenWidth*0.5f)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(PrimaryVioletLittleDarker.value)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Text(
                    text = "Enter your registered email below to receive password reset instructions.",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    fontSize = 15.sp
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    leadingIcon = {
                        Row(
                            modifier = Modifier.padding(start = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.enveloppe),
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Spacer(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(PrimaryViolet)
                            )
                        }
                    },
                    placeholder = {
                        Text(
                            text = if (showEmptyEmailError) "Please enter your email" else "E-mail Address",
                            color = if (showEmptyEmailError) Color.Red else Color.White
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
                            ),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                cursorColor = Color.Black,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                            )
                )

                Button(
                    onClick = {
                        if (email.isBlank()) {
                            showEmptyEmailError = true
                        } else {
                            showEmptyEmailError = false
                            sendPasswordResetEmail(mutableStateOf(email)) { success ->
                                if (success) {
                                    Toast.makeText(context, "Reset email sent!", Toast.LENGTH_SHORT).show()
                                    onVerifyCode(email)
                                } else {
                                    errorMessage = "This email is not registered."
                                    showAlert = true
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "Send Reset Code",
                        color = Color(PrimaryVioletLittleDarker.value),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        TextButton(
            onClick = onBackLogin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(
                text = "Remember password? Login",
                color = PrimaryVioletDark,
                fontSize = 15.sp,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "© 2025 Signify. All rights reserved.",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(bottom = 20.dp)
        )
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text(text = "Email Not Registered") },
            text = { Text(text = errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showAlert = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}