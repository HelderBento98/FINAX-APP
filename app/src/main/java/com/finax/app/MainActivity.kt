package com.finax.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.finax.app.ui.navigation.AppNavigation
import com.finax.app.ui.theme.FinaxTheme
import com.finax.app.ui.theme.IosBlue
import com.finax.app.ui.theme.IosSecondaryText

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FinaxTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                    NotificationPermissionGate()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-check the subscription state (e.g. after returning from the Play purchase flow).
        (application as? FinaxApp)?.billingManager?.queryPurchases()
    }
}

/**
 * Shows a Portuguese, iOS-style pre-permission dialog explaining why the app
 * wants to send notifications, and only then triggers the system request.
 */
@Composable
private fun NotificationPermissionGate() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var showDialog by remember { mutableStateOf(!granted) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        granted = result
        showDialog = false
    }

    if (showDialog && !granted) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = IosBlue)
            },
            title = {
                Text(
                    "Ativar notificações",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    "O FINAX pode te lembrar dos seus compromissos e visitas agendadas. " +
                        "Deseja receber essas notificações?",
                    fontSize = 15.sp,
                    color = IosSecondaryText
                )
            },
            confirmButton = {
                TextButton(onClick = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                    Text("Permitir", color = IosBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Agora não", color = IosSecondaryText, fontSize = 16.sp)
                }
            },
            containerColor = Color.White
        )
    }
}
