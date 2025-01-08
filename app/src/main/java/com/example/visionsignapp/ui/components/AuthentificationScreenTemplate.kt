package com.example.visionsignapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.theme.DarkTextColor
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker
import kotlinx.coroutines.launch

@Composable
fun AuthentificationScreenTemplate(
    modifier: Modifier = Modifier,
    //backgroundGradient: Array<Pair<Float, Color>>,
    @DrawableRes imgRes: Int,
    //title: String,
    //subtitle: String,
    mainActionButtonTile: String,
    secondaryActionButtonTitle: String,
    mainActionButtonColors: ButtonColors,
    secondaryActionButtonColors: ButtonColors,
    actionButtonShadow: Color,
    fullName: MutableState<String>,
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    onMainActionButtonClicked: () -> Unit,
    onSecondaryActionButtonClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    showForgotPassword: Boolean = true, // New parameter to control visibility
    showNameField: Boolean = false,
    fullNameError: String = "",
    emailError: String = "",
    passwordError: String = ""
) {
    var passwordObscure by remember { mutableStateOf(true) } // Corrected to var to make it mutable
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(keyboardHeight) {
        coroutineScope.launch {
            //scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(scrollState)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imgRes),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )
        //Message(title = title, subtitle = subtitle)
        //Text(text = title, fontSize = (screenWidth * 0.15f).value.sp, color = Color.White, modifier = Modifier.offset(y = (-20).dp))
        //Spacer(modifier = Modifier.height(10.dp))

        if (showNameField) {
            InputField(
                leadingIconRes = R.drawable.gens,
                placeholderText = if (fullNameError.isEmpty()) "Your Name" else "Please enter your name",
                inputState = fullName,
                isError = fullNameError.isNotEmpty(),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        InputField(
            leadingIconRes = R.drawable.gens,
            placeholderText = if (emailError.isEmpty()) "Your Email" else "Please enter a valid email",
            inputState = emailState,
            isError = emailError.isNotEmpty(),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        InputField(
            leadingIconRes = R.drawable.cle,
            placeholderText = if (passwordError.isEmpty()) "Your Password" else "Please enter a password",
            inputState = passwordState,
            visualTransformation = if (passwordObscure) PasswordVisualTransformation() else VisualTransformation.None,
            isError = passwordError.isNotEmpty(),
            modifier = Modifier.padding(horizontal = 24.dp),
            trailingIcon = {
                Icon(
                    painter = if (passwordObscure) painterResource(id = R.drawable.ic_outline_visibility_off)
                    else painterResource(id = R.drawable.ic_outline_visibility),
                    contentDescription = "Toggle Password Visibility",
                    modifier = Modifier.clickable {
                        passwordObscure = !passwordObscure
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(screenHeight*0.01f))

        if (showForgotPassword) {
            Text(
                text = "Forgot Password ?",
                fontSize = (screenWidth * 0.04f).value.sp,
                color = Color(PrimaryVioletDark.value),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clickable { onForgotPasswordClicked() }
                    .wrapContentWidth(Alignment.End),
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(screenHeight*0.01f))

        ActionButton(
            text = mainActionButtonTile,
            isNavigationArrowVisible = true,
            onClicked = onMainActionButtonClicked,
            colors = ButtonColors(contentColor = Color.White, containerColor = Color(PrimaryVioletDark.value), disabledContentColor = Color(PrimaryVioletLittleDarker.value), disabledContainerColor = Color(PrimaryVioletLittleDarker.value)),
            shadowColor = actionButtonShadow,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(screenHeight*0.01f))

        OrDivider()

        SocialLoginButtons()

        Spacer(modifier = Modifier.height(screenHeight*0.01f))

        Text(
            text = secondaryActionButtonTitle,
            fontSize = (screenWidth * 0.04f).value.sp,
            color = Color(PrimaryVioletDark.value),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .clickable { onSecondaryActionButtonClicked() }
                .wrapContentWidth(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OrDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "or",
            color = Color.Gray,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )
        Divider(
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun Message(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = subtitle,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun Separator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DashedLine(modifier = Modifier.weight(1f))
        Text(
            text = "Or",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
        DashedLine(modifier = Modifier.weight(1f))
    }
}

@Composable
fun DashedLine(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        drawLine(
            color = Color.White,
            start = Offset(0f, 0f),
            end = Offset(canvasWidth, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    @DrawableRes leadingIconRes: Int,
    placeholderText: String,
    inputState: MutableState<String>,
    isError: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null // Add trailingIcon parameter
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp)
            .border(
                width = 2.dp,
                color = if (isError) Color.Red else Color.Transparent,
                shape = RoundedCornerShape(5)
            ),
        value = inputState.value,
        onValueChange = { inputState.value = it },
        visualTransformation = visualTransformation,
        singleLine = true,
        shape = RoundedCornerShape(percent = 20),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedTextColor = DarkTextColor,
            unfocusedTextColor = DarkTextColor,
            focusedLeadingIconColor = DarkTextColor,
            unfocusedLeadingIconColor = DarkTextColor,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
        leadingIcon = {
            Icon(
                painter = painterResource(leadingIconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        placeholder = {
            Text(
                text = placeholderText,
                color = if (isError) Color.Red else Color.Gray
            )
        },
        trailingIcon = trailingIcon // Apply trailingIcon here
    )
}

@Composable
fun SocialLoginButtons() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        SocialLoginButton(
            icon = painterResource(id = R.drawable.facebook),
            contentDescription = "Facebook Login",
            backgroundColor = Color.Transparent
        )

        SocialLoginButton(
            icon = painterResource(id = R.drawable.google),
            contentDescription = "Google Login",
            backgroundColor = Color.Transparent
        )

        SocialLoginButton(
            icon = painterResource(id = R.drawable.github),
            contentDescription = "Github Login",
            backgroundColor = Color.Transparent
        )
    }
}

@Composable
fun SocialLoginButton(
    icon: Painter,
    contentDescription: String,
    backgroundColor: Color
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier.size(screenHeight*0.1f)
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}