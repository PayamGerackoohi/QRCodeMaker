package com.payamgr.qrcodemaker.data.model.state

import com.airbnb.mvrx.MavericksState
import com.payamgr.qrcodemaker.data.model.QrCodeType

data class ContentTypeState(
    val qrCodeTypes: List<QrCodeType> = listOf(),
) : MavericksState
