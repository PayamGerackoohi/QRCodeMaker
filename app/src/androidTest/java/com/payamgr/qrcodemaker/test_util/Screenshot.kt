package com.payamgr.qrcodemaker.test_util

import android.graphics.Bitmap

@Suppress("EnumEntryName")
enum class Screenshot {
    Splashscreen,
    ContentForm_InsertMode_Text,
    ContentForm_InsertMode_PhoneCall,
    ContentForm_InsertMode_MeCard,
    ContentForm_EditMode,
    ContentType,
    Home,
    ShowQrCode_Portrait,
    ShowQrCode_Portrait_Fullscreen,
    ShowQrCode_Landscape,
    ShowQrCode_Landscape_Fullscreen,
    ShowQrCode_Toolbox,
    ShowQrCode_Toolbox_RemoveContent,
}

fun Screenshot.take(
    fileExtensions: String = "webp",
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP_LOSSY,
    qualityPercentage: Int = 85,
    minDimension: Int = 350,
) = takeScreenshot(
    this,
    fileExtensions,
    compressFormat,
    qualityPercentage,
    minDimension,
)
