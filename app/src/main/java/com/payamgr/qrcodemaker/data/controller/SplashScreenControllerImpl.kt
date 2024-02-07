package com.payamgr.qrcodemaker.data.controller

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplashScreenControllerImpl @Inject constructor() : SplashScreenController {
    override val delay: Long = 500L
    override val duration: Long = 1500L
    override val totalTime: Long get() = delay + duration
    override var keep: Boolean = true
    override fun hide() {
        keep = false
    }
}
