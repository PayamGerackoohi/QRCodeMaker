package com.payamgr.qrcodemaker.view.page.splashscreen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.payamgr.qrcodemaker.view.module.SplashScreen

object Splashscreen {
    @Composable
    fun Page(
        isUsingGoogleSplashScreen: Boolean,
        duration: Long,
        keepSplashScreen: Boolean,
        hideSplashScreen: () -> Unit,
        animationStart: suspend () -> Unit = {},
        target: @Composable () -> Unit,
    ) {
        if (isUsingGoogleSplashScreen) target()
        else {
            var showSplash by remember { mutableStateOf(keepSplashScreen) }
            Crossfade(
                targetState = showSplash,
                label = "App Content Cross-fade",
                animationSpec = tween(500)
            ) { isSplash ->
                Content(
                    duration = duration,
                    isSplash = isSplash,
                    hideSplash = {
                        showSplash = false
                        hideSplashScreen()
                    },
                    target = target,
                    animationStart = animationStart,
                )
            }
        }
    }

    @Composable
    fun Content(
        duration: Long,
        isSplash: Boolean,
        hideSplash: () -> Unit,
        animationStart: suspend () -> Unit = {},
        target: @Composable () -> Unit,
    ) {
        if (isSplash)
            SplashScreen.Module(duration = duration, onEnd = hideSplash, start = animationStart)
        else
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                target()
            }
    }
}
