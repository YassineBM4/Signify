package com.example.visionsignapp.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.components.AppTextField
import com.example.visionsignapp.ui.remote.resetPassword
import com.example.visionsignapp.ui.screen.container.NavGraph
import com.example.visionsignapp.ui.theme.PrimaryPink
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    navController: NavHostController,
    onBackToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var password by remember { mutableStateOf("") }
    var passwordObscure by remember { mutableStateOf(true) }

    var passwordConfirm by remember { mutableStateOf("") }
    var passwordConfirmObscure by remember { mutableStateOf(true) }

    var isPasswordReset by remember { mutableStateOf(false) }
    var resetPasswordState by remember { mutableStateOf<Result<Unit>?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset Your Password",
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
            contentDescription = "Reset Password Icon",
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
                    text = "Enter your new password.",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    fontSize = 18.sp
                )
                AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    hint = "New Password",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "New Password Field",
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = if (passwordObscure) painterResource(id = R.drawable.ic_outline_visibility_off)
                            else painterResource(id = R.drawable.ic_outline_visibility),
                            contentDescription = "Toggle New Password Visibility",
                            modifier = Modifier.clickable {
                                passwordObscure = !passwordObscure
                            }
                        )
                    },
                    obscure = passwordObscure,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 10.dp)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = passwordConfirm,
                    onValueChange = { passwordConfirm = it },
                    hint = "Confirm Password",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Re-enter Password Field",
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = if (passwordConfirmObscure) painterResource(id = R.drawable.ic_outline_visibility_off)
                            else painterResource(id = R.drawable.ic_outline_visibility),
                            contentDescription = "Toggle Re-enter Password Visibility",
                            modifier = Modifier.clickable {
                                passwordConfirmObscure = !passwordConfirmObscure
                            }
                        )
                    },
                    obscure = passwordConfirmObscure,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 10.dp)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!isPasswordReset) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                resetPasswordState = resetPassword(EMAIL, password)
                                if (resetPasswordState?.isSuccess == true) {
                                    isPasswordReset = true
                                    Toast.makeText(context, "You have successfully changed your password", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to reset password", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Submit",
                            color = Color(PrimaryVioletLittleDarker.value),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    navController.navigate("login_screen") {
                        popUpTo(NavGraph.ResetPassword.route) { inclusive = true }
                        isPasswordReset = false
                    }
                    /*Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onBackToLogin,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(PrimaryVioletLittleDarker.value)),
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)

                    ) {
                        Text(text = "Sign In", color = Color.White)
                    }*/
                }
            }
        }
    }
}
