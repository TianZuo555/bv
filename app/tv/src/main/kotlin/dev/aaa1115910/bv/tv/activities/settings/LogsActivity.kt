package dev.aaa1115910.bv.tv.activities.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.tv.screens.settings.LogsScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class LogsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                LogsScreen()
            }
        }
    }
}