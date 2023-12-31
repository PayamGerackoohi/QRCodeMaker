package com.payamgr.qrcodemaker.data

import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.model.QrCode

object QrCodeMaker {
    init {
        System.loadLibrary("qrcodemaker")
    }

    external fun text(text: String, ecc: Int): QrCode
    external fun phoneCall(phone: String, ecc: Int): QrCode
    external fun meCard(meCard: MeCardContent, ecc: Int): QrCode
}
