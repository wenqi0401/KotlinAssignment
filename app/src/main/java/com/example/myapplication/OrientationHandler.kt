package com.example.myapplication

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun OrientationAwareContent(
    portraitContent: @Composable () -> Unit,
    landscapeContent: @Composable () -> Unit = portraitContent
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLandscape) {
            landscapeContent()
        } else {
            portraitContent()
        }
    }
}

@Composable
fun SimpleLandscapeWrapper(
    content: @Composable () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(0.6f)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

