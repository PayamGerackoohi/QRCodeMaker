package com.payamgr.qrcodemaker.view.page.splashscreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.airbnb.mvrx.mocking.MockableMavericks
import com.payamgr.qrcodemaker.test_util.app
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.sync.Mutex
import org.junit.Rule
import org.junit.Test

class SplashscreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun page_usingTheGoogleSplashScreen_shouldShowTheCalculatorPage() {
        rule.setContent {
            Splashscreen.Page(
                isUsingGoogleSplashScreen = true,
                duration = 0L,
                keepSplashScreen = false,
                hideSplashScreen = {},
            ) {
                DummyContentPage()
            }
        }

        // Verify the initial state
        rule.onNodeWithText("Content Page").assertIsDisplayed()
        rule.onAllNodesWithContentDescription("Splashscreen").assertCountEquals(0)
    }

    @Test
    fun page_notUsingTheGoogleSplashScreen_and_notKeepSplashScreen_shouldShowSkipTheCustomSplashscreen() {
        rule.setContent {
            Splashscreen.Page(
                isUsingGoogleSplashScreen = true,
                duration = 0L,
                keepSplashScreen = false,
                hideSplashScreen = {},
            ) {
                DummyContentPage()
            }
        }

        // Verify the initial state
        rule.onNodeWithText("Content Page").assertIsDisplayed()
        rule.onAllNodesWithContentDescription("Splashscreen").assertCountEquals(0)
    }

    @Test
    fun page_notUsingTheGoogleSplashScreen_shouldShowTheCustomSplashscreen_and_TransitionToTheCalculatorPage() {
        val animationMutex = Mutex(true)
        val hideSplashScreen = mockk<() -> Unit>()
        justRun { hideSplashScreen() }

        MockableMavericks.initialize(app)
        rule.setContent {
            Splashscreen.Page(
                isUsingGoogleSplashScreen = false,
                duration = 10L,
                keepSplashScreen = true,
                hideSplashScreen = hideSplashScreen,
                animationStart = { animationMutex.lock() },
            ) {
                DummyContentPage()
            }
        }

        // Verify the initial state
        rule.onNodeWithContentDescription("Splashscreen").assertIsDisplayed()
        rule.onAllNodesWithText("Content Page").assertCountEquals(0)

        // Wait until the end of the animation
        animationMutex.unlock()
        rule.waitUntil {
            rule.onAllNodesWithText("Content Page").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify the transition to the 'Calculator' page
        rule.onNodeWithText("Content Page").assertIsDisplayed()
        rule.onAllNodesWithContentDescription("Splashscreen").assertCountEquals(0)
        verify { hideSplashScreen() }

        confirmVerified()
    }

    @Test
    fun content_isSplashMode_shouldShowTheSplashscreen() {
        var isHideSplashCalled = false
        val hideSplash = mockk<() -> Unit>()
        justRun { hideSplash() }

        rule.setContent {
            Splashscreen.Content(
                duration = 10L,
                isSplash = true,
                hideSplash = {
                    hideSplash()
                    isHideSplashCalled = true
                },
            ) {
                DummyContentPage()
            }
        }

        // Verify the initial state
        rule.onNodeWithContentDescription("Splashscreen").assertIsDisplayed()
        rule.onAllNodesWithText("Content Page").assertCountEquals(0)

        // Wait until the end of the animation
        rule.waitUntil { isHideSplashCalled }

        // Verify the 'hideSplash' callback is called
        verify { hideSplash() }

        confirmVerified()
    }

    @Test
    fun content_notSplashMode_shouldShowTheCalculatorPage() {
        rule.setContent {
            Splashscreen.Content(
                duration = 0L,
                isSplash = false,
                hideSplash = {},
            ) {
                DummyContentPage()
            }
        }

        // Verify the initial state
        rule.onNodeWithText("Content Page").assertIsDisplayed()
        rule.onAllNodesWithContentDescription("Splashscreen").assertCountEquals(0)
    }

    @Composable
    @Suppress("TestFunctionName")
    private fun DummyContentPage() {
        Text(text = "Content Page")
    }
}
