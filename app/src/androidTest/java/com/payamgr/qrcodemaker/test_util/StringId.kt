package com.payamgr.qrcodemaker.test_util

import androidx.annotation.StringRes

data class StringId(@StringRes val resource: Int) {
    val label by lazy { app.getString(resource) }
}
