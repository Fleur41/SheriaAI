package com.sam.sheriaapp.presentation.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sam.sheriaapp.domain.model.Account
import com.sam.sheriaapp.domain.model.AccountType
import com.sam.sheriaapp.domain.model.Activity
import com.sam.sheriaapp.presentation.viewmodel.AccountViewModel
import java.io.File
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavHostController,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val currentAccount = state.currentAccount
    var searchQuery by remember { mutableStateOf("") }


    //Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.handleImageSelection(context, it)
//            val imageUriString = it.toString()
//            viewModel.updateProfileImage(imageUriString)
        }
    }
    // Permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // Permissions granted, launch image picker
            imagePicker.launch("image/*")
        }
    }

    // Function to handle image selection with permission check
    val onImageSelect = {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        ))
    }

    LaunchedEffect(Unit) {
        viewModel.searchAccounts(searchQuery)
    }
    Scaffold(
        topBar = {
            AccountTopBar(
                navController = navController,
                currentAccount = currentAccount,
                totalAccounts = state.totalAccounts,
                currentIndex = state.currentAccountIndex,
                onNext = { viewModel.nextAccount() },
                onPrevious = { viewModel.previousAccount() },
                searchQuery = searchQuery,
                onSearchQueryChange = { query ->
                    searchQuery = query
                },
                onClearSearch = {
                    searchQuery = ""
                    viewModel.clearSearch()
                }
            )
        }
    ) { innerPadding ->
        if (currentAccount != null) {
            AccountContent(
                account = currentAccount,
                onImageSelect = onImageSelect,
                //onImageSelect = { imagePicker.launch("image/*") },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(state.error ?: "No account data available")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTopBar(
    navController: NavHostController,
    currentAccount: Account?,
    totalAccounts: Int,
    currentIndex: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.height(140.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Account title row with navigation arrows
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back to Dashboard",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        "Account",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = onPrevious, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBackIos,
                            "Previous",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(onClick = onNext, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            "Next",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search text field - always visible
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        onSearchQueryChange(query)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    placeholder = {
                        Text(
                            "Search for law firms...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier.size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                onClick = onClearSearch,
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.error,
                                shadowElevation = 24.dp
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        navigationIcon = {
            // Navigation icon is now part of the title, so we can leave this empty
        },
        actions = {
            // Account indicator dots
            if (totalAccounts > 1) {
                Row(modifier = Modifier.padding(end = 8.dp)) {
                    repeat(totalAccounts) { index ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .padding(2.dp)
                                .background(
                                    color = if (index == currentIndex)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        Color.Gray.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    )
}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AccountTopBar(
//    navController: NavHostController,
//    currentAccount: Account?,
//    totalAccounts: Int,
//    currentIndex: Int,
//    onNext: () -> Unit,
//    onPrevious: () -> Unit,
//    searchQuery: String,
//    onSearchQueryChange: (String) -> Unit,
//    onClearSearch: () -> Unit
//) {
//    var showSearch by remember { mutableStateOf(false) }
//
//    TopAppBar(
//        title = {
//            if (showSearch) {
//                // Search text field
//                OutlinedTextField(
//                    value = searchQuery,
//                    onValueChange = { query ->
//                        onSearchQueryChange(query)
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp),
//                    placeholder = {
//                        Text(
//                            "Search for law firms...",
//                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                    },
//                    leadingIcon = {
//                        Box(
//                            modifier = Modifier.size(40.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Search,
//                                contentDescription = "Search",
//                                tint = MaterialTheme.colorScheme.primary,
//                                modifier = Modifier.size(24.dp)
//                            )
//                        }
//                    },
//                    trailingIcon = {
//                        if (searchQuery.isNotEmpty()) {
//                            Surface(
//                                modifier = Modifier.size(40.dp),
//                                onClick = onClearSearch,
//                                shape = CircleShape,
//                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
//                                contentColor = MaterialTheme.colorScheme.error,
//                                shadowElevation = 24.dp
//                            ) {
//                                Box(
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.Clear,
//                                        contentDescription = "Clear search",
//                                        tint = MaterialTheme.colorScheme.error,
//                                        modifier = Modifier.size(24.dp)
//                                    )
//                                }
//                            }
//                        } else {
//                            IconButton(
//                                onClick = { showSearch = false }
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Close,
//                                    contentDescription = "Close search",
//                                    modifier = Modifier.size(24.dp)
//                                )
//                            }
//                        }
//                    },
//                    singleLine = true,
//                    textStyle = MaterialTheme.typography.bodyLarge,
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedBorderColor = MaterialTheme.colorScheme.primary,
//                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
//                    ),
//                    shape = RoundedCornerShape(12.dp)
//                )
//            } else {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    IconButton(onClick = onPrevious, modifier = Modifier.size(24.dp)) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBackIos, "Previous", modifier = Modifier.size(16.dp))
//                    }
//
//                    Spacer(modifier = Modifier.weight(1f))
//
//                    Text("Account", modifier = Modifier.padding(horizontal = 16.dp))
//
//                    Spacer(modifier = Modifier.weight(1f))
//
//                    IconButton(onClick = onNext, modifier = Modifier.size(24.dp)) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, "Next", modifier = Modifier.size(16.dp))
//                    }
//                }
//            }
//        },
//        navigationIcon = {
//            if (showSearch) {
//                // Empty navigation icon when searching
//            } else {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to Dashboard")
//                }
//            }
//        },
//        actions = {
//            if (!showSearch) {
//                // Search icon
//                IconButton(onClick = { showSearch = true }) {
//                    Icon(Icons.Default.Search, "Search accounts")
//                }
//            }
//
//            if (totalAccounts > 1 && !showSearch) {
//                Row(modifier = Modifier.padding(end = 8.dp)) {
//                    repeat(totalAccounts) { index ->
//                        Box(
//                            modifier = Modifier
//                                .size(6.dp)
//                                .padding(2.dp)
//                                .background(
//                                    color = if (index == currentIndex)
//                                        MaterialTheme.colorScheme.primary
//                                    else
//                                        Color.Gray.copy(alpha = 0.5f),
//                                    shape = CircleShape
//                                )
//                        )
//                    }
//                }
//            }
//        }
//    )
//}

@Composable
fun AccountContent(
    account: Account,
    onImageSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Profile Header
        ProfileHeader(account = account, onImageSelect = onImageSelect)

        Spacer(modifier = Modifier.height(24.dp))

        // Registration Details
        SectionTitle("Registration Details")
        DetailItem("Email", account.email)
        DetailItem("Phone", account.phone)
        DetailItem("Address", account.address)

        // Financial Data for Law Firm Admins
        account.financialData?.let { financialData ->
            Spacer(modifier = Modifier.height(16.dp))
            DetailItem("Total Spent", "$${financialData.totalSpent}")
            financialData.avgSpendingPerUser?.let { avg ->
                DetailItem("Avg. Spending Per User", "$${String.format("%.2f", avg)}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Subscription Plan
        SectionTitle("Subscription Plan")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(56.dp)
        ) {
            Column {
                Text("Plan", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(account.subscription.plan, style = MaterialTheme.typography.bodyMedium)
            }
            Column {
                Text("Status", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(
                    account.subscription.status,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        account.subscription.lastPayment?.let { lastPayment ->
            DetailItem("Last Payment", lastPayment.format(DateTimeFormatter.ISO_DATE))
        }

        // Financial Distribution for Law Firm Admins
        account.financialData?.spendingDistribution?.let { distribution ->
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Spending Distribution")
            Text("$${distribution.totalAmount}", style = MaterialTheme.typography.headlineSmall)
            Text(
                "${distribution.period} +${account.financialData?.growthPercentage}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
            // User breakdown
            distribution.userBreakdown.forEach { (user, amount) ->
                DetailItem(user, "$${String.format("%.2f", amount)}")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Date Range", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Activity Log
        SectionTitle("Activity Log")
        account.activities.forEach { activity ->
            ActivityItem(activity)
        }

        Spacer(modifier = Modifier.height(34.dp))

        // Action Buttons - Dynamic based on account type
        ActionButtons(account.type)
    }
}

@Composable
fun ProfileHeader(account: Account, onImageSelect: () -> Unit) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            if (!account.profileImageUri.isNullOrEmpty()) {
                // Use remember to prevent recomposition issues
                val imageUri = remember(account.profileImageUri) {
                    // Handle both file paths and content URIs
                    if (account.profileImageUri!!.startsWith("content://") ||
                        account.profileImageUri!!.startsWith("file://")) {
                        Uri.parse(account.profileImageUri)
                    } else {
                        Uri.fromFile(File(account.profileImageUri))
                    }
                }

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                    contentScale = ContentScale.Crop,
                    error = @Composable {
                        // Fallback if image fails to load
                        DefaultProfileIcon()
                    } as Painter?
                )
            } else {
                DefaultProfileIcon()
            }

            // Add photo button
            IconButton(
                onClick = onImageSelect,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Photo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(account.name, style = MaterialTheme.typography.headlineSmall)
        Text(
            "Registered on ${account.registrationDate.format(DateTimeFormatter.ISO_DATE)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

//@Composable
//fun AccountContent(
//    account: Account,
//    onImageSelect: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val scrollState = rememberScrollState()
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .verticalScroll(scrollState)
//            .padding(16.dp)
//    ) {
//        // Profile Header
//        ProfileHeader(account = account, onImageSelect = onImageSelect)
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Registration Details
//        SectionTitle("Registration Details")
//        DetailItem("Email", account.email)
//        DetailItem("Phone", account.phone)
//        DetailItem("Address", account.address)
//
//        // Financial Data for Law Firm Admins
//        account.financialData?.let { financialData ->
//            Spacer(modifier = Modifier.height(16.dp))
//            DetailItem("Total Spent", "$${financialData.totalSpent}")
//            financialData.avgSpendingPerUser?.let { avg ->
//                DetailItem("Avg. Spending Per User", "$${String.format("%.2f", avg)}")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Subscription Plan
//        SectionTitle("Subscription Plan")
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.spacedBy(56.dp)
//        ) {
//            Column {
//                Text("Plan", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
//                Text(account.subscription.plan, style = MaterialTheme.typography.bodyMedium)
//            }
//            Column {
//                Text("Status", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
//                Text(
//                    account.subscription.status,
//                    style = MaterialTheme.typography.bodyMedium,
//                    //color = if (account.subscription.status == "Active") Color.Green else Color.Red
//                )
//            }
//        }
//        account.subscription.lastPayment?.let { lastPayment ->
//            DetailItem("Last Payment", lastPayment.format(DateTimeFormatter.ISO_DATE))
//        }
//
//        // Financial Distribution for Law Firm Admins
//        account.financialData?.spendingDistribution?.let { distribution ->
//            Spacer(modifier = Modifier.height(24.dp))
//            SectionTitle("Spending Distribution")
//            Text("$${distribution.totalAmount}", style = MaterialTheme.typography.headlineSmall)
//            Text(
//                "${distribution.period} +${account.financialData?.growthPercentage}%",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.primary
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//            // User breakdown
//            distribution.userBreakdown.forEach { (user, amount) ->
//                DetailItem(user, "$${String.format("%.2f", amount)}")
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//            Text("Date Range", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Activity Log
//        SectionTitle("Activity Log")
//        account.activities.forEach { activity ->
//            ActivityItem(activity)
//        }
//
//        Spacer(modifier = Modifier.height(34.dp))
//
//        // Action Buttons - Dynamic based on account type
//        ActionButtons(account.type)
//    }
//}

//@Composable
//fun ProfileHeader(account: Account, onImageSelect: () -> Unit) {
//    val context = LocalContext.current
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Box {
//            if (!account.profileImageUri.isNullOrEmpty()) {
//                // Use remember to prevent recomposition issues
//                val imageUri = remember(account.profileImageUri) {
//                    // Handle both file paths and content URIs
//                    if (account.profileImageUri!!.startsWith("content://") ||
//                        account.profileImageUri!!.startsWith("file://")) {
//                        Uri.parse(account.profileImageUri)
//                    } else {
//                        Uri.fromFile(File(account.profileImageUri))
//                    }
//                }
//
//                AsyncImage(
//                    model = ImageRequest.Builder(context)
//                        .data(imageUri)
//                        .crossfade(true)
//                        .build(),
//                    contentDescription = "Profile Image",
//                    modifier = Modifier
//                        .size(120.dp)
//                        .clip(CircleShape)
//                        .border(2.dp, Color.Gray, CircleShape),
//                    contentScale = ContentScale.Crop,
//                    error = @androidx.compose.runtime.Composable {
//                        // Fallback if image fails to load
//                        DefaultProfileIcon()
//                    } as Painter?
//                )
//            } else {
//                DefaultProfileIcon()
//            }
//
//            // Add photo button
//            IconButton(
//                onClick = onImageSelect,
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .size(36.dp)
//                    .background(MaterialTheme.colorScheme.primary, CircleShape)
//            ) {
//                Icon(
//                    Icons.Default.Add,
//                    "Add Photo",
//                    tint = Color.White,
//                    modifier = Modifier.size(20.dp)
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(account.name, style = MaterialTheme.typography.headlineSmall)
//        Text(
//            "Registered on ${account.registrationDate.format(DateTimeFormatter.ISO_DATE)}",
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//        )
//    }
//}
//Added this fn
@Composable
fun DefaultProfileIcon() {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Default Profile",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .padding(32.dp),
        tint = Color.White
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ActivityItem(activity: Activity) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            activity.description,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            activity.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun ActionButtons(accountType: AccountType) {
    val commonActions = listOf(
        "Edit Account",
        "Deactivate Account",
        "Export Account Data"
    )
//    val commonActions = listOf(
//        "Edit Account" to Icons.Default.Edit,
//        "Deactivate Account" to Icons.Default.Warning,
//        "Export Account Data" to Icons.Default.Download
//    )

    val specificActions = when (accountType) {
        is AccountType.LawFirmAdmin -> listOf(
            "Send Payment Reminder"
//            "Send Payment Reminder" to Icons.Default.Payment
        )
        else -> emptyList()
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        (commonActions + specificActions).forEach { text ->
//        (commonActions + specificActions).forEach { (text, icon) ->
            Button(
                onClick = { /* Handle action */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
//                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
//
                Text(text, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

