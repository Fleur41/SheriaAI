package com.sam.sheriaapp.presentation.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "RequestsScreen") })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
                    label = { Text("Dashboard") },
                    selected = currentRoute == "dashboard",
                    onClick = { navController.navigate("dashboard") }
                )

                NavigationBarItem(
                    icon = { Icon(imageVector = Icons.Default.People, contentDescription = null) },
                    label = { Text("Users") },
                    selected = currentRoute == "users",
                    onClick = { navController.navigate("users") }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Description, contentDescription = null) },
                    label = { Text("Requests") },
                    selected = currentRoute == "requests",
                    onClick = { navController.navigate("requests") }
                )

                NavigationBarItem( //Analytics
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    label = { Text("Analysts") },
                    selected = currentRoute == "analysts",
                    onClick = { navController.navigate("analysts") }
                )

                NavigationBarItem( //RequestQuote
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                    label = { Text("Billing") },
                    selected = currentRoute == "billing",
                    onClick = { navController.navigate("billing") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {}) {
                Text(text = "Requests")
            }
        }
    }
}
