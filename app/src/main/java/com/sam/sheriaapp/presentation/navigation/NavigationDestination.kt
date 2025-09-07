package com.sam.sheriaapp.presentation.navigation

sealed interface NavigationDestination {
    val title: String
    val route: String

    object Dashboard : NavigationDestination {
        override val title = "Dashboard"
        override val route = "dashboard"
    }
    object Users : NavigationDestination {
        override val title = "Users"
        override val route = "users"
    }
    object Requests : NavigationDestination {
        override val title = "Requests"
        override val route = "requests"
    }
    object Analysts : NavigationDestination {
        override val title = "Analysts"
        override val route = "analysts"
    }
    object Billing : NavigationDestination {
        override val title = "Billing"
        override val route = "billing"
    }

    object Account : NavigationDestination {
        override val title = "Account"
        override val route = "account"
    }
}