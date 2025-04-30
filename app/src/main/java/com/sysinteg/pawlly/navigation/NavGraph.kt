package com.sysinteg.pawlly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sysinteg.pawlly.ui.screens.HomeScreen
import com.sysinteg.pawlly.ui.screens.LandingScreen
import com.sysinteg.pawlly.ui.screens.LoginScreen
import com.sysinteg.pawlly.ui.screens.ProfileScreen
import com.sysinteg.pawlly.ui.screens.SettingsScreen
import com.sysinteg.pawlly.ui.screens.SignUpScreen
import com.sysinteg.pawlly.ui.screens.NotificationScreen

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Notifications : Screen("notifications")
    // Adopt flow
    object AdoptHome : Screen("adopt")
    object AdoptResults : Screen("adopt/results")
    object AdoptPetDetail : Screen("adopt/pet/{id}")
    object AdoptStep1 : Screen("adopt/start")
    object AdoptStep2 : Screen("adopt/address")
    object AdoptStep3 : Screen("adopt/home")
    object AdoptStep4 : Screen("adopt/images")
    object AdoptStep5 : Screen("adopt/roommate")
    object AdoptStep6 : Screen("adopt/other-animals")
    object AdoptStep7 : Screen("adopt/confirm")
    object AdoptFinish : Screen("adopt/finish")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Landing.route
    ) {
        composable(Screen.Landing.route) {
            LandingScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { email, password -> 
                    // TODO: Implement actual login logic
                    navController.navigate(Screen.Home.route) 
                },
                onForgotPasswordClick = { /* TODO: handle forgot password */ },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) },
                onGoogleSignInClick = { /* TODO: handle Google sign in */ }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpClick = { firstName, lastName, username, email, password, phoneNumber, address, confirmPassword, profilePictureUri -> 
                    // TODO: Implement actual signup logic
                    navController.navigate(Screen.Home.route) 
                },
                onGoogleSignInClick = { /* TODO: handle Google sign in */ },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onAdoptClick = { navController.navigate(Screen.AdoptHome.route) },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = { navController.navigate(Screen.Login.route) },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) }
            )
        }
        composable(Screen.Notifications.route) {
            NotificationScreen(
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                selectedScreen = "Notifications"
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        // Adopt flow
        composable(Screen.AdoptHome.route) {
            com.sysinteg.pawlly.ui.screens.AdoptScreen(
                onBrowseAll = { navController.navigate(Screen.AdoptResults.route) },
                onPetClick = { id -> navController.navigate("adopt/pet/$id") },
                onFilterClick = { /* TODO: filter logic */ },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable(Screen.AdoptResults.route) {
            com.sysinteg.pawlly.ui.screens.AdoptSearchResultsScreen(
                onPetClick = { id -> navController.navigate("adopt/pet/$id") },
                onBack = { navController.popBackStack() },
                onFilter = { /* TODO: filter logic */ },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable("adopt/pet/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
            com.sysinteg.pawlly.ui.screens.AdoptPetDetailScreen(
                petId = id,
                onAdoptNow = { navController.navigate(Screen.AdoptStep1.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep1.route) {
            com.sysinteg.pawlly.ui.screens.AdoptAdoptionStep1Screen(
                onStart = { navController.navigate(Screen.AdoptStep2.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep2.route) {
            com.sysinteg.pawlly.ui.screens.AdoptAdoptionStep2Screen(
                onContinue = { navController.navigate(Screen.AdoptStep3.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep3.route) {
            com.sysinteg.pawlly.ui.screens.AdoptAdoptionStep3Screen(
                onContinue = { navController.navigate(Screen.AdoptStep4.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep4.route) {
            com.sysinteg.pawlly.ui.screens.AdoptAdoptionStep4Screen(
                onContinue = { navController.navigate(Screen.AdoptStep5.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep5.route) {
            com.sysinteg.pawlly.ui.screens.AdoptAdoptionStep5Screen(
                onContinue = { navController.navigate(Screen.AdoptStep6.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep6.route) {
            com.sysinteg.pawlly.ui.screens.AdoptAdoptionStep6Screen(
                onContinue = { navController.navigate(Screen.AdoptStep7.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep7.route) {
            com.sysinteg.pawlly.ui.screens.AdoptAdoptionStep7Screen(
                onReturnToProfile = { navController.navigate(Screen.Profile.route) },
                onAdoptMore = { navController.navigate(Screen.AdoptHome.route) }
            )
        }
        composable(Screen.AdoptFinish.route) {
            com.sysinteg.pawlly.ui.screens.AdoptAdoptionFinishScreen(
                onReturnToProfile = { navController.navigate(Screen.Profile.route) },
                onAdoptMore = { navController.navigate(Screen.AdoptHome.route) }
            )
        }
    }
} 