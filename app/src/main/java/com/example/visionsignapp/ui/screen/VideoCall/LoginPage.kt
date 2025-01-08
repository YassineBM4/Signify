package com.example.visionsignapp.ui.screen.VideoCall

import android.content.Intent
import android.os.Bundle
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.interactivevideocall.VideoActivity
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.components.CustomLevelBar
import com.example.visionsignapp.ui.remote.UserPreferences
import com.example.visionsignapp.ui.remote.fetchProfile
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker

@Composable
fun LoginPage(navController: NavController, userPreferences: UserPreferences) {
    var fullName by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(0) }
    var gems by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val channelNameState = remember { mutableStateOf(TextFieldValue()) }
    val userRoleOptions = listOf("Broadcaster", "Audience")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(userRoleOptions[0]) }
    var showDialog by remember { mutableStateOf(false) } // For the popup
    var channelNameError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val iconSize = screenWidth * 0.15f
    val fontSize = screenWidth * 0.05f
    val coinSize = screenWidth * 0.12f
    val textPadding = screenWidth * 0.02f

    LaunchedEffect(Unit) {
        fetchProfile(userPreferences) { user ->
            fullName = user.fullName ?: "Full name not available"
            icon = user.icon ?: ""
            level = user.level ?: 0
            gems = user.gems ?: 0
        }
    }

    androidx.compose.material3.Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { BottomNavigationBar(navController) },
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryViolet.copy(alpha = 0.9f),
                        PrimaryViolet.copy(alpha = 0.3f)
                    )
                )
            )
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top section: User information
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
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
                        modifier = Modifier
                            .size(iconSize)
                            .clickable { navController.navigate("profile_screen") }
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "$fullName ~ lvl.$level",
                            fontSize = fontSize.value.sp, // Dynamically calculated font size
                            color = Color(PrimaryVioletDark.value),
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
                            fontSize = (fontSize.value * 0.9f).sp, // Slightly smaller than name font size
                            color = Color(PrimaryVioletDark.value)
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
            Spacer(modifier = Modifier.height(16.dp))
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp.dp / (1.5f))
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){
                Image(
                    painter = painterResource(id = R.drawable.hand_sign),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        //.aspectRatio(1.5f)
                        .height(LocalConfiguration.current.screenHeightDp.dp / (2.25f))
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Let's Video Call !",
                    modifier = Modifier.padding(horizontal = 30.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    fontSize = 30.sp,
                    lineHeight = 30.sp,
                    color = Color(PrimaryVioletDark.value)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Connect with your friends in a channel and enjoy real-time AI-powered hand sign translation to text!",
                    modifier = Modifier.padding(horizontal = 30.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { showDialog = true },
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    colors = buttonColors(
                         Color(PrimaryVioletLittleDarker.value)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.6f)
                ) {
                    Text(text = "Join Now !", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                }
            }

            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth((LocalConfiguration.current.screenHeightDp.dp * 0.9f).value)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextField(
                                value = channelNameState.value,
                                onValueChange = { channelNameState.value = it },
                                label = { if (channelNameError) {
                                    Text(
                                        text = "Please enter your channel name",
                                        color = Color.Red
                                    )
                                } else {
                                    Text("Channel Name")
                                } },
                                placeholder = { Text("Enter the channel name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            userRoleOptions.forEach { text ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = (text == selectedOption),
                                            onClick = { onOptionSelected(text) }
                                        )
                                ) {
                                    RadioButton(
                                        selected = (text == selectedOption),
                                        onClick = { onOptionSelected(text) },
                                        colors = RadioButtonDefaults.colors(Color(PrimaryVioletLittleDarker.value))
                                    )
                                    Text(
                                        text = text,
                                        modifier = Modifier.padding(start = 8.dp),
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.08f),
                                shape = RoundedCornerShape(16.dp),
                                colors = buttonColors(
                                    Color(PrimaryVioletLittleDarker.value)
                                ),
                                onClick = {
                                    if (channelNameState.value.text.isBlank()) {
                                        channelNameError = true
                                    } else {
                                        val intent = Intent(context, VideoActivity::class.java)
                                        intent.putExtra("ChannelName", channelNameState.value.text)
                                        intent.putExtra("UserRole", selectedOption)
                                        ContextCompat.startActivity(context, intent, Bundle())
                                        showDialog = false
                                    }
                                }
                            ) {
                                Text(text = "Start the call", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Lessons,
        NavigationItem.VideoCall,
        NavigationItem.Profile
    )

    NavigationBar(
        containerColor = Color(PrimaryVioletLittleDarker.value),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { androidx.compose.material3.Icon(item.icon(), contentDescription = item.title) },
                label = { androidx.compose.material3.Text(item.title) },
                colors = NavigationBarItemColors(
                    selectedIconColor = Color(PrimaryVioletLittleDarker.value),
                    selectedTextColor = Color.White,
                    selectedIndicatorColor = Color.White,
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White,
                    disabledIconColor = Color.Gray,
                    disabledTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

sealed class NavigationItem(val route: String, val title: String, val icon: @Composable () -> Painter) {
    object Home : NavigationItem("home_screen", "Home", { painterResource(id = R.drawable.baseline_home_24) })
    object Lessons : NavigationItem("lesson_screen", "Lessons", { painterResource(id = R.drawable.baseline_cast_for_education_24) })
    object VideoCall : NavigationItem("login_page", "Video Call", { painterResource(id = R.drawable.baseline_add_call_24) })
    object Profile : NavigationItem("profile_screen", "Profile", { painterResource(id = R.drawable.baseline_face_24) })
}