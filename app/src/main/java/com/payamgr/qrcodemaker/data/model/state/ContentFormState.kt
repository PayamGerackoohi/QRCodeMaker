package com.payamgr.qrcodemaker.data.model.state

import com.airbnb.mvrx.MavericksState
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.QrCodeType

data class ContentFormState(
    val currentQrCodeType: QrCodeType? = null,
    val currentContent: Content? = null,
    val contentTitle: String = "?",
) : MavericksState
