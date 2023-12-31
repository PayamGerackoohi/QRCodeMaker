package com.payamgr.qrcodemaker.data.model.state

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.PersistState
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.data.model.QrCode

data class ShowQrCodeState(
    val currentContent: Content? = null,
    val qrCode: QrCode = QrCode(size = 0, data = booleanArrayOf()),
    @PersistState val ecc: ErrorCorrectionCodeLevel = ErrorCorrectionCodeLevel.Medium,
) : MavericksState
