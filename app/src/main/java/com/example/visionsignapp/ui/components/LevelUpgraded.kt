package com.example.visionsignapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.remote.UserPreferences
import com.example.visionsignapp.ui.remote.fetchProfile
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.PrimaryVioletLight

@Composable
fun HomePage(navController: NavController, userPreferences: UserPreferences) {
    var fullName by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(0) }
    var gems by remember { mutableStateOf(0) }

    // Fetch user data
    LaunchedEffect(Unit) {
        fetchProfile(userPreferences) { user ->
            fullName = user.fullName ?: "Full name not available"
            level = user.level ?: 0
            gems = user.gems ?: 0
        }
    }

    // Home screen
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .background(color = Color(PrimaryVioletLight.value))
                .offset(y = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Row content remains the same
            Image(
                painter = painterResource(id = R.drawable.kayn),
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .offset(y = 5.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = fullName,
                    fontSize = 25.sp,
                    color = Color(PrimaryVioletDark.value),
                    modifier = Modifier.padding(start = 5.dp, top = 10.dp)
                )
                CustomLevelBar(userPreferences)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.signify_coin),
                    contentDescription = "Signify Coin",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent) // Ensure the background of the image is transparent
                        .padding(top = 10.dp, end = 5.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = gems.toString() + " SC",
                    fontSize = 18.sp,
                    color = Color(PrimaryVioletDark.value),
                    modifier = Modifier.offset(y = (0).dp)
                )
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent) // Ensure the entire Column is transparent
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Let's Get Started !",
                fontWeight = FontWeight.SemiBold,
                fontSize = 35.sp,
                color = Color(PrimaryVioletDark.value),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Unlock your next level",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color(PrimaryViolet.value)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(201.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(width = 5.dp, color = Color(PrimaryViolet.value), shape = CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ziro),
                    contentDescription = "Level zero",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(shape = CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CustomLevelBar(userPreferences)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "SC "+gems.toString() +"/100")
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .width(150.dp),
                onClick = { /* Navigate to lessons or other actions */ }) {
                Text(
                    text = "Get Started!",
                    fontSize = 15.sp
                )
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
        containerColor = Color(PrimaryVioletLight.value),
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
                icon = { Icon(item.icon(), contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemColors(
                    selectedIconColor = Color(PrimaryVioletLight.value),
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
    object VideoCall : NavigationItem("video_call_screen", "Video Call", { painterResource(id = R.drawable.baseline_add_call_24) })
    object Profile : NavigationItem("profile_screen", "Profile", { painterResource(id = R.drawable.baseline_face_24) })
}


