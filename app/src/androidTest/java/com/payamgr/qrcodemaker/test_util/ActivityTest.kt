package com.payamgr.qrcodemaker.test_util

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Before
import org.junit.Rule

abstract class ActivityTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        rule.hideActionBar()
    }
}
