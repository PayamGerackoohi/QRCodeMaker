package com.payamgr.qrcodemaker.data.model.state

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.PersistState
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.QrCodeType

data class ContentFormState(
    val currentQrCodeType: QrCodeType? = null,
    val currentContent: Content? = null,
//    @PersistState
    val contentTitle: String = "?",
) : MavericksState
// todo cleanup
