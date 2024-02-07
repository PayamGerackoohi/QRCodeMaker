package com.payamgr.qrcodemaker.view.module

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import com.payamgr.qrcodemaker.R
import org.junit.Rule
import org.junit.Test

class AnimatedVectorTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun shouldBeDisplayed() {
        var atEnd by mutableStateOf(false)
        rule.setContent {
            AnimatedVector.Module(
                resourceId = R.drawable.splash_screen_icon,
                atEnd = atEnd,
                label = "Sample",
                modifier = Modifier.size(100.dp)
            )
        }

        // Verify the initial state
        rule.onNodeWithContentDescription("Animated Vector: Sample")
        rule.onNode(hasStateDescription("Beginning")).assertExists()

        // Toggle the 'atEnd'
        atEnd = true

        // Verify the state is updated
        rule.onNode(hasStateDescription("End")).assertExists()
    }
}
