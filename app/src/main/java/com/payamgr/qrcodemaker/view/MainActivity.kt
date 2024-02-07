package com.payamgr.qrcodemaker.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.Mavericks
import com.payamgr.qrcodemaker.data.di.provideAppModule
import com.payamgr.qrcodemaker.view.page.splashscreen.Splashscreen
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import com.payamgr.qrcodemaker.view.util.isAndroidSdk31plus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * It controls how long the splash screen is kept.
     *
     * Normal @Inject provides the object after "super.onCreate", so [provideAppModule] is used.
     */
    private val splashScreenController by lazy { provideAppModule().splashScreenController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isAndroidSdk31plus) setupSplashScreen()
        super.onCreate(savedInstanceState)
        Mavericks.initialize(this)
        setContent {
            QRCodeMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Splashscreen.Page(
                        isUsingGoogleSplashScreen = isAndroidSdk31plus,
                        duration = splashScreenController.totalTime,
                        keepSplashScreen = splashScreenController.keep,
                        hideSplashScreen = splashScreenController::hide,
                    ) {
                        AppNav.Host(navController = rememberNavController())
                    }
                }
            }
        }
    }

    private fun setupSplashScreen() {
        installSplashScreen().setKeepOnScreenCondition { splashScreenController.keep }
        lifecycleScope.launch {
            delay(splashScreenController.duration)
            splashScreenController.hide()
        }
    }
}
