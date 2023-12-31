package com.payamgr.qrcodemaker.data.model.event

import com.payamgr.qrcodemaker.data.model.Content

sealed class HomeEvent {
    data class NavigateToShowQrCode(val content: Content) : HomeEvent()
    object NavigateToContentType : HomeEvent()
}
