package com.example.visionsignapp.ui.screen.container

sealed class NavGraph(val route: String) {
    data object Welcome : NavGraph(route = "welcome_screen")
    data object Login : NavGraph(route = "login_screen")
    data object Registration : NavGraph(route = "registration_screen")
    data object ForgotPassword : NavGraph(route = "forgot_password_screen")
    data object ResetPassword : NavGraph(route = "reset_password_screen")
    data object VerificationCodeScreen : NavGraph(route = "verification_code_screen/{email}") {
        fun createRoute(email: String) = "verification_code_screen/$email"
    }
    data object Profile : NavGraph(route = "profile_screen")
    data object Home : NavGraph(route = "home_screen")
    data object Lessons : NavGraph(route = "lesson_screen")
    data object Onboarding : NavGraph(route = "onboarding_screen")
    data object Content : NavGraph(route = "content_screen/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: String, categoryName: String) = "content_screen/$categoryId/$categoryName"
    }
    data object Quiz : NavGraph(route = "quiz_screen/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: String, categoryName: String) = "quiz_screen/$categoryId/$categoryName"
    }
    data object Result : NavGraph(route = "result_screen")
    data object Shop : NavGraph(route = "shop_screen")
    data object Room : NavGraph(route = "login_page")
}