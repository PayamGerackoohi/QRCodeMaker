package com.payamgr.qrcodemaker.view.module

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.payamgr.qrcodemaker.R
import kotlinx.coroutines.delay

@Preview
@Composable
fun SplashScreen_Preview() {
    Column {
        SplashScreen.Module(duration = 0L, {}) {}
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun SplashScreen_Landscape_Preview() {
    Column {
        SplashScreen.Module(duration = 0L, {}) {}
    }
}

object SplashScreen {
    @Composable
    fun Module(duration: Long, onEnd: () -> Unit, start: suspend () -> Unit = {}) {
        val splashscreenText = stringResource(id = R.string.splashscreen)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.splash_background))
                .semantics { contentDescription = splashscreenText }
        ) {
            var atEnd by remember { mutableStateOf(false) }
            LaunchedEffect(null) {
                start()
                atEnd = true
                delay(duration)
                onEnd()
            }
            Box(
                modifier = Modifier
                    .background(color = Color.White, shape = CircleShape)
                    .size(150.dp)
                    .testTag("SplashScreen.IconBackground")
            )
            AnimatedVector.Module(
                resourceId = R.drawable.splash_screen_icon,
                atEnd = atEnd,
                label = stringResource(id = R.string.splashscreen_icon),
                modifier = Modifier.size(200.dp)
            )
        }
    }
}
