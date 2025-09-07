package com.sam.sheriaapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sam.sheriaapp.presentation.navigation.AppNavigation
import com.sam.sheriaapp.presentation.navigation.NavigationDestination
import com.sam.sheriaapp.ui.theme.SheriaAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SheriaAppTheme { // Your specific theme (SheriaAppTheme or SheriaAITheme)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Call your main application composable
                    SheriaAppNavigation()
                }
            }
        }
    }
}

@Composable
fun SheriaAppNavigation() {
    val navController = rememberNavController()
    val startDestination = NavigationDestination.Dashboard
    AppNavigation(
        navController = navController,
        startDestination = startDestination
    )
}

//@Composable
//// Utility function to check and request permissions
//fun checkStoragePermissions(activity: Activity, onGranted: () -> Unit) {
//    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        arrayOf(
//            Manifest.permission.READ_MEDIA_IMAGES,
//            Manifest.permission.READ_MEDIA_VIDEO
//        )
//    } else {
//        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
//    }
//
//    if (permissions.all {
//            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
//        }) {
//        onGranted()
//    } else {
//        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_STORAGE)
//    }
//}


