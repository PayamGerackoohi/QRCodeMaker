package com.payamgr.qrcodemaker.data

import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.model.QrCode

object NativeQrCodeMakerImpl: NativeQrCodeMaker {
    init {
        System.loadLibrary("qrcodemaker")
    }

    external override fun text(text: String, ecc: Int): QrCode
    external override fun phoneCall(phone: String, ecc: Int): QrCode
    external override fun meCard(meCard: MeCardContent, ecc: Int): QrCode
}
