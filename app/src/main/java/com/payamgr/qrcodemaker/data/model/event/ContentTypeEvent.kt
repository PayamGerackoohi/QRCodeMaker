package com.payamgr.qrcodemaker.data.model.event

import com.payamgr.qrcodemaker.data.model.QrCodeType

sealed class ContentTypeEvent {
    data class NavigateToContentForm(val type: QrCodeType) : ContentTypeEvent()
}
