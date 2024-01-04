package com.payamgr.qrcodemaker.data

import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.model.QrCode

interface NativeQrCodeMaker {
    fun text(text: String, ecc: Int): QrCode
    fun phoneCall(phone: String, ecc: Int): QrCode
    fun meCard(meCard: MeCardContent, ecc: Int): QrCode
}
