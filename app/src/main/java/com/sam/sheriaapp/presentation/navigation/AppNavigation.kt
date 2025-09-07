package com.sam.sheriaapp.presentation.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sam.sheriaapp.presentation.screens.DashboardScreen
import androidx.navigation.compose.NavHost     // <-- ADD THIS IMPORT
import androidx.navigation.compose.composable
import com.sam.sheriaapp.presentation.screens.AccountScreen
import com.sam.sheriaapp.presentation.screens.AnalystsScreen
import com.sam.sheriaapp.presentation.screens.BillingScreen
import com.sam.sheriaapp.presentation.screens.RequestsScreen
import com.sam.sheriaapp.presentation.screens.UsersScreen
import com.sam.sheriaapp.presentation.viewmodel.BillingViewModel


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation (
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: NavigationDestination
){

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination.route
    ){
        composable(route = NavigationDestination.Dashboard.route){
            DashboardScreen(navController)
        }
        composable(NavigationDestination.Users.route) {
            UsersScreen(navController)
        }
        composable(NavigationDestination.Requests.route) {
            RequestsScreen(navController)
        }
        composable(NavigationDestination.Analysts.route) {
            AnalystsScreen(navController)
        }
        composable(NavigationDestination.Billing.route) {
            val viewModel: BillingViewModel = hiltViewModel()
            BillingScreen(viewModel, navController )
        }
        composable(NavigationDestination.Account.route) {
            AccountScreen(navController)
        }
    }
}