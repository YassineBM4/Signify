package com.example.visionsignapp.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.remote.RetrofitInstance
import com.example.visionsignapp.ui.remote.fetchAndVerifyCode
import com.example.visionsignapp.ui.theme.PrimaryPink
import com.example.visionsignapp.ui.theme.PrimaryVioletLight
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationCodeScreen(
    email: String,
    onVerifyCodeSuccess: () -> Unit
) {
    val authApi = RetrofitInstance.getAuthApi()
    var verificationCode by remember { mutableStateOf("") }
    var showAlert by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val isVerifying = remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    fun onCodeChanged(newValue: String) {
        if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
            verificationCode = newValue
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Verify Your Email",
            color = PrimaryPink,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            fontWeight = FontWeight.Bold,
            fontSize = (screenWidth*0.08f).value.sp
        )

        Image(
            painter = painterResource(id = R.drawable.pass),
            contentDescription = "Verification Icon",
            modifier = Modifier.size(screenWidth*0.5f)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(PrimaryVioletLittleDarker.value)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 20.dp)) {
                Text(
                    text = "Enter the verification code sent to your email address.",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    fontSize = 14.sp
                )

                TextField(
                    value = verificationCode,
                    onValueChange = { onCodeChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    placeholder = { Text(text = "Enter Verification Code", color = Color.Gray) },
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
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
                        isVerifying.value = true
                        fetchAndVerifyCode(
                            api = authApi,
                            email = email,
                            resetCode = verificationCode,
                            onSuccess = {
                                isVerifying.value = false
                                onVerifyCodeSuccess()
                            },
                            onFailure = { error ->
                                isVerifying.value = false
                                errorMessage = error
                                showAlert = true
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isVerifying.value
                ) {
                    if (isVerifying.value) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Verify Code",
                            color = Color(PrimaryVioletLittleDarker.value),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text(text = "Invalid Verification Code") },
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
