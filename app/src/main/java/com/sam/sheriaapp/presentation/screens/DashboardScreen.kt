package com.sam.sheriaapp.presentation.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sam.sheriaapp.domain.model.Dashboard
import com.sam.sheriaapp.presentation.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(text = "DashBoard")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("account")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Account Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, //Transparent
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
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
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatsCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Law Firms",
                    value = state.totalLawFirms.toString()
                )
                Spacer(modifier = Modifier.width(16.dp))
                StatsCard(
                    modifier = Modifier.weight(1f),
                    title = "Active Users",
                    value = state.activeUsers.toString()
                )

            }
            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                StatsCard(
                    modifier = Modifier.weight(1f),
                    title = "Research Requests",
                    value = state.researchRequests.toString()
                )
                Spacer(modifier = Modifier.width(16.dp))

                StatsCard(
                    modifier = Modifier.weight(1f),
                    title = "Revenue",
                    value = "$${state.revenue}"
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(state.recentActivities) { activity ->
                    RecentActivityItem(activity = activity)
                }
            }
        }
    }
}

@Composable
private fun StatsCard(modifier: Modifier = Modifier, title: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun RecentActivityItem(activity: Dashboard.RecentActivity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (activity.type) {
                        Dashboard.ActivityType.NEW_LAW_FIRM -> Icons.Default.Add
                        Dashboard.ActivityType.NEW_USER -> Icons.Default.Add
                        Dashboard.ActivityType.RESEARCH_REQUEST -> Icons.Default.RequestQuote

                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    modifier = Modifier.padding(start = 40.dp, top = 4.dp),
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
