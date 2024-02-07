package com.payamgr.qrcodemaker.data.controller

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SplashScreenControllerImplTest {
    private lateinit var controller: SplashScreenController

    @BeforeEach
    fun setup() {
        controller = SplashScreenControllerImpl()
    }

    @Test
    fun `Verify the constants`() {
        assertThat(controller.delay).isEqualTo(500L)
        assertThat(controller.duration).isEqualTo(1500L)
        assertThat(controller.totalTime).isEqualTo(2000L)
    }

    @Test
    fun `Verify the hide action`() {
        // Verify the initial state
        assertThat(controller.keep).isEqualTo(true)
        controller.hide()
        assertThat(controller.keep).isEqualTo(false)
    }
}
