package com.goblins.launcher1280

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.goblins.launcher1280.model.LauncherController
import com.goblins.launcher1280.model.LauncherScreen
import com.goblins.launcher1280.ui.GoblinsKeypadAssembly
import com.goblins.launcher1280.ui.GoblinsLcdAssembly
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var controller: LauncherController

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (::controller.isInitialized) {
                controller.refreshApps()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        controller = LauncherController(applicationContext)

        val packageFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        ContextCompat.registerReceiver(
            this,
            packageReceiver,
            packageFilter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    controller.onBackPressed()
                }
            },
        )

        setContent {
            val state = controller.uiState

            LaunchedEffect(Unit) {
                while (true) {
                    controller.refreshClockAndStatus()
                    delay(10_000L)
                }
            }

            LaunchedEffect(state.screen is LauncherScreen.SnakeGame) {
                while (controller.uiState.screen is LauncherScreen.SnakeGame) {
                    delay(175L)
                    controller.tickSnake()
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0D0B))
                    .navigationBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            ) {
                GoblinsLcdAssembly(
                    state = state,
                    controller = controller,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.72f),
                )

                GoblinsKeypadAssembly(
                    theme = state.theme,
                    onKeyPress = controller::onKeyPress,
                    onKeyLongPress = controller::onKeyLongPress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.28f)
                        .padding(top = 6.dp),
                )
            }
        }
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(packageReceiver) }
        if (::controller.isInitialized) {
            controller.release()
        }
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (::controller.isInitialized) {
            controller.refreshClockAndStatus()
            controller.refreshApps()
        }
    }
}
