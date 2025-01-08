package com.example.visionsignapp.ui.screen.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.components.CustomLevelBar
import com.example.visionsignapp.ui.remote.Category
import com.example.visionsignapp.ui.remote.User
import com.example.visionsignapp.ui.remote.UserPreferences
import com.example.visionsignapp.ui.remote.fetchCategories
import com.example.visionsignapp.ui.remote.fetchProfile
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@Composable
fun HomePage(navController: NavController, userPreferences: UserPreferences) {
    var fullName by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(0) }
    var gems by remember { mutableStateOf(0) }
    val context = LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val iconSize = screenWidth * 0.15f
    val fontSize = screenWidth * 0.05f
    val coinSize = screenWidth * 0.12f
    val textPadding = screenWidth * 0.02f

    BackHandler {
        (context as? Activity)?.finishAffinity()
    }

    LaunchedEffect(Unit) {
        fetchProfile(userPreferences) { user ->
            fullName = user.fullName ?: "Full name not available"
            icon = user.icon ?: ""
            level = user.level ?: 0
            gems = user.gems ?: 0
        }
    }

    Scaffold(
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
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    fetchProfile(userPreferences) { user ->
                        fullName = user.fullName ?: "Full name not available"
                        icon = user.icon ?: ""
                        level = user.level ?: 0
                        gems = user.gems ?: 0
                    }
                    refreshKey++ // Increment key to refresh ProgressList
                    isRefreshing = false
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceBetween,
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

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Your Categories :",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                    )
                    ProgressList(userPreferences = userPreferences, refreshKey = refreshKey, navController)
                }

                Spacer(modifier = Modifier.height(10.dp))
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
                icon = { Icon(item.icon(), contentDescription = item.title) },
                label = { Text(item.title) },
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

@Composable
fun ProgressList(userPreferences: UserPreferences, refreshKey: Int, navController: NavController) {
    val userProfile = remember { mutableStateOf<User?>(null) }
    val categories = produceState<List<Category>?>(initialValue = null, key1 = refreshKey) {
        fetchCategories { data -> value = data }
    }.value

    LaunchedEffect(userPreferences, refreshKey) {
        fetchProfile(userPreferences) { profile ->
            userProfile.value = profile
        }
    }

    if (categories.isNullOrEmpty() || userProfile.value == null) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // Loading state
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                ProgressCard(item = category, userProfile = userProfile.value!!, navController)
            }
        }
    }
}

@Composable
fun ProgressCard(item: Category, userProfile: User, navController: NavController) {
    val categoryProgress = userProfile.categoriesProgress?.firstOrNull { it.category?.catId == item.catId }
    val progress = categoryProgress?.progress ?: 0
    val isLocked = categoryProgress?.isLocked ?: true
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isLocked == true) Color(PrimaryViolet.value) else Color(
                        PrimaryVioletLittleDarker.value
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                .clickable {
                    navController.navigate("lesson_screen")
                }
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.catName ?: "Nothing to show",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isLocked == true) Color.LightGray else Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (isLocked == false) "Unlocked" else "Locked",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isLocked == true) Color.LightGray else Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (progress == 0) {
                        Text(
                            text = item.catDescription ?: "Nothing to describe",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isLocked == true) Color.LightGray else Color.White
                        )
                    } else if (progress in 0..69) {
                        Text(
                            text = "You can do it !",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isLocked == true) Color.LightGray else Color.White
                        )
                    } else if (progress == 70) {
                        Text(
                            text = "Good Job !",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isLocked == true) Color.LightGray else Color.White
                        )
                    }
                    Text(
                        text = String.format("%.1f%%", (progress / 70f) * 100f),
                        fontSize = 16.sp,
                        color = if (isLocked == true) Color.LightGray else Color.White
                    )
                }
                LinearProgressIndicator(
                    progress = (progress) / 70f,
                    color = Color(PrimaryViolet.value),
                    trackColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
}