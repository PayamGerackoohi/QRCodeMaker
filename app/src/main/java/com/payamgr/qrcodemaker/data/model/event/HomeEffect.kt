package com.payamgr.qrcodemaker.data.model.event

import com.payamgr.qrcodemaker.data.model.Content

sealed class HomeEffect {
    data class NavigateToShowQrCode(val content: Content) : HomeEffect()
    object NavigateToContentType : HomeEffect()
}
