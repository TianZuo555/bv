package dev.aaa1115910.bv.tv.screens.settings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.tv.component.settings.SettingsMenuSelectItem
import dev.aaa1115910.bv.player.entity.VideoCodec
import dev.aaa1115910.bv.tv.screens.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.util.Prefs

@Composable
fun VideoCodecSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedVideoCodec by remember { mutableStateOf(Prefs.defaultVideoCodec) }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = SettingsMenuNavItem.VideoCodec.getDisplayName(context),
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = VideoCodec.entries.filter { it != VideoCodec.DVH1 && it != VideoCodec.HVC1 }) { videoCodec ->
                    SettingsMenuSelectItem(
                        text = videoCodec.getDisplayName(context),
                        selected = selectedVideoCodec == videoCodec,
                        onClick = {
                            selectedVideoCodec = videoCodec
                            Prefs.defaultVideoCodec = videoCodec
                        }
                    )
                }
            }
        }
    }
}
