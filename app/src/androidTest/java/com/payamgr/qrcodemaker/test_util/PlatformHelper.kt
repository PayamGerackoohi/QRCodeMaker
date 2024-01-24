package com.payamgr.qrcodemaker.test_util

import android.app.UiAutomation
import android.content.Context
import android.graphics.Bitmap.CompressFormat
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.core.graphics.scale
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import kotlin.math.min

private const val SCREENSHOTS_DIRECTORY = "sdcard/Documents/QrCodeMaker/screenshots"
val app: Context get() = InstrumentationRegistry.getInstrumentation().targetContext

fun ComposeContentTestRule.requestOrientation(rotation: Int) {
    InstrumentationRegistry.getInstrumentation().uiAutomation.setRotation(rotation)
    waitForIdle()
}

fun ComposeContentTestRule.requestPortraitOrientation() = requestOrientation(UiAutomation.ROTATION_FREEZE_0)
fun ComposeContentTestRule.requestLandscapeOrientation() = requestOrientation(UiAutomation.ROTATION_FREEZE_90)

fun takeScreenshot(
    screenshot: Screenshot,
    fileExtensions: String,
    compressFormat: CompressFormat,
    qualityPercentage: Int,
    minDimension: Int,
) {
    val fileName = screenshot.name
    File(
        File(SCREENSHOTS_DIRECTORY).apply { if (!exists()) mkdirs() },
        "$fileName.$fileExtensions"
    ).apply {
        if (exists() && !delete())
            println(
                "*** Failed to take screenshot for \"$fileName\"! " +
                        "Please call the \"take-screenshots.sh\" to " +
                        "bypass the unnecessary IO permission issues."
            )
        else outputStream().use {
            InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot().apply {
                if (min(width, height) < minDimension)
                    compress(compressFormat, qualityPercentage, it)
                else {
                    getTargetDimensions(minDimension, width, height).let { (width, height) ->
                        scale(width, height)
                    }.apply {
                        compress(compressFormat, qualityPercentage, it)
                        recycle()
                    }
                }
                recycle()
            }
        }
    }
}

private fun getTargetDimensions(minDimension: Int, width: Int, height: Int) = if (width < height)
    minDimension to minDimension * height / width
else
    minDimension * width / height to minDimension
