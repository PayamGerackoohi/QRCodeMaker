package com.payamgr.qrcodemaker.data

import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.data.model.QrCode

interface QrCodeMaker {
    suspend fun encode(content: Content, errorCorrectionCodeLevel: ErrorCorrectionCodeLevel): QrCode
}
