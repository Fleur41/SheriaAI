package com.sam.sheriaapp.presentation.screens

import android.R.attr.text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sam.sheriaapp.domain.model.Billing
import com.sam.sheriaapp.presentation.viewmodel.BillingViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Locale.getDefault


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    viewModel: BillingViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Billing",
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
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

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    label = { Text("Analysts") },
                    selected = currentRoute == "analysts",
                    onClick = { navController.navigate("analysts") }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                    label = { Text("Billing") },
                    selected = currentRoute == "billing",
                    onClick = { navController.navigate("billing") }
                )
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: ${uiState.error}")
            }
        } else {
            BillingContent(
                modifier = Modifier.padding(innerPadding),
                currentPlan = uiState.currentPlan,
                paymentMethod = uiState.paymentMethod,
                billingHistory = uiState.billingHistory
            )
        }
    }
}

@Composable
fun BillingContent(
    modifier: Modifier = Modifier,
    currentPlan: Billing?,
    paymentMethod: String,
    billingHistory: List<Billing>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Title - Fixed from "Bullying" to "Billing"
//        Text(
//            text = "Billing",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )

        // Subscription Section
        Text(
            text = "Subscription",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        currentPlan?.let { plan ->
            // Plan name and amount on the same line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${plan.planName} Plan")
                Text(
                    text = "$${plan.amount}/month",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Status below
            Text(
                text = plan.planStatus,
                color = if (plan.planStatus.contains("Active", ignoreCase = true)) Color.Gray else Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Payment Method Section
        Text(
            text = "Payment Method",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Payment Method Card (aligned to left)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Card icon and Visa text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "Credit Card",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Visa **** 4242",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Expiry date below
                Text(
                    text = "Expires 08/2025",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Transaction History Section
        Text(
            text = "Transaction History",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        billingHistory.forEach { billing ->
            TransactionItem(billing = billing)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
@Composable
fun TransactionItem(billing: Billing) {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(billing.billingDate)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side: Plan name and date
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${billing.planName} Plan",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formattedDate,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        // Right side: Amount
        Text(
            text = "$${billing.amount}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BillingScreen(
//    viewModel: BillingViewModel = hiltViewModel(),
//    navController: NavHostController
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Box( modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
//                        Text(text = "Billing")
//                    }
//                },
//                navigationIcon = {
//                    IconButton(onClick = {navController.popBackStack()}) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = null
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White, //Transparent
//                    titleContentColor = MaterialTheme.colorScheme.primary
//                )
//            )
//        },
//        bottomBar = {
//            NavigationBar {
//                NavigationBarItem(
//                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
//                    label = { Text("Dashboard") },
//                    selected = currentRoute == "dashboard",
//                    onClick = { navController.navigate("dashboard") }
//                )
//
//                NavigationBarItem(
//                    icon = { Icon(imageVector = Icons.Default.People, contentDescription = null) },
//                    label = { Text("Users") },
//                    selected = currentRoute == "users",
//                    onClick = { navController.navigate("users") }
//                )
//
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Description, contentDescription = null) },
//                    label = { Text("Requests") },
//                    selected = currentRoute == "requests",
//                    onClick = { navController.navigate("requests") }
//                )
//
//                NavigationBarItem( //Analytics
//                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
//                    label = { Text("Analysts") },
//                    selected = currentRoute == "analysts",
//                    onClick = { navController.navigate("analysts") }
//                )
//
//                NavigationBarItem( //RequestQuote
//                    icon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
//                    label = { Text("Billing") },
//                    selected = currentRoute == "billing",
//                    onClick = { navController.navigate("billing") }
//                )
//            }
//        }
//    ) { innerPadding ->
////        Column(
////            modifier = Modifier
////                .fillMaxSize()
////                .padding(innerPadding),
////            verticalArrangement = Arrangement.Center,
////            horizontalAlignment = Alignment.CenterHorizontally
////        ) {
////            Text("Billing Screen")
////            Icon(
////                imageVector = Icons.Default.CreditCard,
////                contentDescription = null,
////                modifier = Modifier.size(100.dp)
////            )
////        }
//        if(uiState.isLoading){
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding),
//                contentAlignment = Alignment.Center
//            ){
//                CircularProgressIndicator()
//            }
//        } else if(uiState.error != null){
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding),
//                contentAlignment = Alignment.Center
//            ){
//                Text(
//                    text = "Error: ${ uiState.error }",
//                    color = Color.Red,
//                    textAlign = TextAlign.Center
//                )
//
//            }
//        } else {
//            BillingContent(
//                modifier = Modifier.padding(innerPadding),
//                currentPlan = uiState.currentPlan,
//                paymentMethod = uiState.paymentMethod,
//                billingHistory = uiState.billingHistory
//            )
//        }
//    }
//}
//
//@Composable
//fun BillingContent(
//    modifier: Modifier = Modifier,
//    currentPlan: Billing?,
//    paymentMethod: String,
//    billingHistory: List<Billing>
//) {
//    val scrollState = rememberScrollState()
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .verticalScroll(scrollState)
//            .padding(16.dp)
//    ) {
//        // Title
//        Text(
//            text = "Billing",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        // Subscription Section
//        Text(
//            text = "Subscription",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.SemiBold,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//
//        currentPlan?.let { plan ->
//            Text(text = "${plan.planName} Plan")
//            Text(
//                text = "$${plan.amount}/${plan.currency.lowercase(Locale.ROOT)}",
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Text(
//                text = plan.planStatus,
//                color = if (plan.planStatus.contains("Active", ignoreCase = true)) Color.Green else Color.Red,
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Payment Method Section
//            Text(
//                text = "Payment Method",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.SemiBold,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//
//            Text(text = paymentMethod)
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Transaction History Section
//            Text(
//                text = "Transaction History",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.SemiBold,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//
//            billingHistory.forEach { billing ->
//                TransactionItem(billing = billing)
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//        }
//    }
//}
//
//@Composable
//fun TransactionItem(billing: Billing)  {
//    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
//    val formattedDate = dateFormat.format(billing.billingDate)
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Column {
//            Text(text = "${billing.planName} Plan")
//            Text(
//                text = "$${billing.amount}",
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//        Text(
//            text = formattedDate,
//            color = Color.Gray
//        )
//    }
//}
