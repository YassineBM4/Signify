package com.example.visionsignapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.visionsignapp.ui.remote.UserPreferences
import com.example.visionsignapp.ui.remote.fetchProfile
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.visionsignapp.R
import com.example.visionsignapp.ui.remote.updateUserPassword
import com.example.visionsignapp.ui.remote.updateUserProfile
import com.example.visionsignapp.ui.theme.PrimaryViolet
import com.example.visionsignapp.ui.theme.PrimaryVioletLight
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.visionsignapp.ui.remote.updateUserIcon
import com.example.visionsignapp.ui.theme.PrimaryVioletDark
import com.example.visionsignapp.ui.theme.PrimaryVioletLittleDarker
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userPreferences: UserPreferences, navController: NavHostController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val editProfileSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editPasswordSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isEditPageVisible by remember { mutableStateOf(false) }
    var isChangePasswordVisible by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(Unit) {
        fetchProfile(userPreferences) { user ->
            email = user.email ?: "Email not available"
            fullName = user.fullName ?: "Full name not available"
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

        fun handleProfileUpdate(updatedFullName: String, updatedEmail: String) {
            coroutineScope.launch {
                try {
                    val authToken = userPreferences.getToken()

                    if (authToken != null) {
                        val result = updateUserProfile(updatedFullName, updatedEmail, authToken)

                        if (result.isSuccess) {
                            email = updatedEmail
                            fullName = updatedFullName
                            isEditPageVisible = false
                            editProfileSheetState.hide()
                        } else {
                            errorMessage = "Failed to update profile. Please try again."
                        }
                    } else {
                        errorMessage = "User is not logged in."
                    }
                } catch (e: Exception) {
                    errorMessage = "An unexpected error occurred: ${e.message}"
                    e.printStackTrace() // Log the error for debugging
                }
            }
        }

        fun handleChangePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
            if (newPassword != confirmPassword) {
                errorMessage = "New password and confirmation do not match."
                return
            }
            coroutineScope.launch {
                try {
                    val authToken = userPreferences.getToken()
                    if (authToken != null) {
                        val result = updateUserPassword(oldPassword, newPassword, authToken)
                        if (result.isSuccess) {
                            isChangePasswordVisible = false
                            editPasswordSheetState.hide()
                        } else {
                            errorMessage =
                                "Failed to update password. ${result.exceptionOrNull()?.message}"
                        }
                    } else {
                        errorMessage = "User is not logged in."
                    }
                } catch (e: Exception) {
                    errorMessage = "An error occurred: ${e.message}"
                    e.printStackTrace()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            ProfileAvatarSelector(userPreferences)
            Text(
                text = fullName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.offset(y = (-10).dp)
            )
            Text(
                text = "Member since - Jan, 2025",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.7F),
                modifier = Modifier.offset(y = (-10).dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    // Personal Information Section
                    SectionHeader(text = "Personal Information") {
                        isEditPageVisible = true
                        coroutineScope.launch {
                            editProfileSheetState.show()
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(PrimaryVioletLittleDarker.value),
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(16.dp)
                    ) {
                        LabelWithValue(label = "Full Name", value = fullName)
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            thickness = 0.5.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LabelWithValue(label = "E-mail Address", value = email)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Utilities Section
                    SectionHeader(text = "Utilities")
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(PrimaryVioletLittleDarker.value),
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(16.dp)
                    ) {
                        UtilityItem(icon = R.drawable.baseline_edit_square_24, text = "Change Password") {
                            isChangePasswordVisible = true
                            coroutineScope.launch { editPasswordSheetState.show() }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            color = Color.White.copy(alpha = 0.5f),
                            thickness = 0.5.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        UtilityItem(icon = R.drawable.baseline_logout_24, text = "Log-Out") {
                            coroutineScope.launch {
                                userPreferences.clearToken()
                                navController.navigate("login_screen")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.1f))
        }

        if (isEditPageVisible) {
                ModalBottomSheet(
                    sheetState = editProfileSheetState,
                    containerColor = PrimaryVioletLittleDarker,
                    contentColor = Color.White,
                    onDismissRequest = {
                        isEditPageVisible = false
                        coroutineScope.launch {
                            editProfileSheetState.hide()
                        }
                    },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
                ) {
                    EditPageContent(
                        fullName = fullName,
                        email = email,
                        onSave = { updatedFullName, updatedEmail ->
                            handleProfileUpdate(updatedFullName, updatedEmail)
                        }
                    )
            }

        }

        if (isChangePasswordVisible) {
            ModalBottomSheet(
                sheetState = editPasswordSheetState,
                containerColor = PrimaryVioletLittleDarker,
                contentColor = Color.White,
                onDismissRequest = {
                    isChangePasswordVisible = false
                    coroutineScope.launch { editPasswordSheetState.hide() }
                },
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
            ) {
                UpdatePasswordPageContent(
                    onSave = { oldPassword, newPassword, confirmPassword ->
                        handleChangePassword(oldPassword, newPassword, confirmPassword)
                    }
                )
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPageContent(fullName: String, email: String, onSave: (String,String) -> Unit) {

    var editedFullName by remember { mutableStateOf(fullName) }
    var editedEmail by remember { mutableStateOf(email) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Your Profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 16.dp)
        )

        TextField(
            value = editedFullName,
            onValueChange = { editedFullName = it },
            label = { Text("Full Name", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = editedEmail,
            onValueChange = { editedEmail = it },
            label = { Text("Email", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSave(editedFullName, editedEmail) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(PrimaryVioletDark.value),
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Save Info", color = Color.White, fontSize = 17.sp)
        }
    }
}

@Composable
fun UpdatePasswordPageContent( onSave: (String, String, String) -> Unit) {

    var currentPassword by remember { mutableStateOf("") }
    var editedPassword by remember { mutableStateOf("") }
    var confirmEditedPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Change Your Password",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 16.dp),
        )

        TextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = editedPassword,
            onValueChange = { editedPassword = it },
            label = { Text("New Password", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = confirmEditedPassword,
            onValueChange = { confirmEditedPassword = it },
            label = { Text("Confirm New Password", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSave(currentPassword, editedPassword, confirmEditedPassword) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(PrimaryVioletDark.value),
            )
        ) {
            Text(text = "Save Password", color = Color.White, fontSize = 17.sp)
        }
    }
}

@Composable
fun SectionHeader(text: String, onEditClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f) // Makes text take remaining space
        )
        if (onEditClick != null) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_edit_24), // Replace with your edit icon
                contentDescription = "Edit",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onEditClick() } // Trigger the edit action
            )
        }
    }
}

@Composable
fun LabelWithValue(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

@Composable
fun UtilityItem(icon: Int, text: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
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
fun ProfileAvatarSelector(userPreferences: UserPreferences) {
    var isAvatarSelectionVisible by remember { mutableStateOf(false) }
    var selectedAvatar by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Fetch the user profile when the composable is first launched
    LaunchedEffect(Unit) {
        fetchProfile(userPreferences) { user ->
            selectedAvatar = user.icon
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color.White, Color.Transparent)
                )
            )
            .offset(y = (-10).dp)
            .clickable {
                isAvatarSelectionVisible = true // Show the avatar selection dialog
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(131.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(width = 2.dp, color = Color(PrimaryVioletLight.value), shape = CircleShape)
        ) {
            if (!selectedAvatar.isNullOrEmpty()) {
                if (selectedAvatar?.startsWith("http") == true) {
                    // If the selected avatar is a URL, load it asynchronously
                    AsyncImage(
                        model = selectedAvatar,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent),
                        placeholder = painterResource(id = R.drawable.man_icon_2) // Placeholder
                    )
                } else {
                    // If it's a drawable name, load it from resources
                    val imageResId = context.resources.getIdentifier(selectedAvatar, "drawable", context.packageName)
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = "Profile Icon",
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                        )
                    } else {
                        // Fallback to default image if the drawable is not found
                        Image(
                            painter = painterResource(id = R.drawable.man_icon_1),
                            contentDescription = "Default Profile Image",
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                        )
                    }
                }
            } else {
                // If no avatar is selected, show a default image
                Image(
                    painter = painterResource(id = R.drawable.profile_icon),
                    contentDescription = "Default Profile Image",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                )
            }
        }
    }

    // Avatar Selection Dialog
    if (isAvatarSelectionVisible) {
        Dialog(onDismissRequest = { isAvatarSelectionVisible = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(220.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Choose Your Icon",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Avatar selection rows for male and female icons
                    listOf(
                        listOf("man_icon_1", "man_icon_2", "man_icon_3"),
                        listOf("woman_icon_1", "woman_icon_2", "woman_icon_3")
                    ).forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            row.forEach { icon ->
                                AvatarOption(
                                    iconName = icon,
                                    userPreferences = userPreferences,
                                    coroutineScope = coroutineScope,
                                    onAvatarSelected = { selectedIcon ->
                                        selectedAvatar = selectedIcon
                                        coroutineScope.launch {
                                            userPreferences.saveAvatarUrl(selectedIcon)
                                        }
                                        isAvatarSelectionVisible = false // Close the dialog
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarOption(iconName: String, userPreferences: UserPreferences, coroutineScope: CoroutineScope, onAvatarSelected: (String) -> Unit)
{
    val context = LocalContext.current

    // Function to get the drawable ID
    fun getDrawableId(iconName: String): Int {
        return context.resources.getIdentifier(iconName, "drawable", context.packageName)
    }

    Image(
        painter = painterResource(id = getDrawableId(iconName)),
        contentDescription = iconName,
        modifier = Modifier
            .size(80.dp)
            .clickable {
                coroutineScope.launch {
                    val authToken = userPreferences.getToken()
                    if (!authToken.isNullOrEmpty()) {
                        val result = updateUserIcon(iconName, authToken)
                        if (result.isSuccess) {
                            onAvatarSelected(iconName)
                        }
                    }
                }
            }
    )
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