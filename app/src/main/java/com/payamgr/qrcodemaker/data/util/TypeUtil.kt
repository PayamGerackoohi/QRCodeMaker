package com.payamgr.qrcodemaker.data.util

import com.payamgr.qrcodemaker.data.model.QrCodeType

object TypeUtil {
    // todo flaky reflection: naively assumes that all the constructors receive string parameters
    val qrCodeTypes: List<QrCodeType> = QrCodeType::class.sealedSubclasses.map { clazz ->
        clazz.constructors.first().run {
            call(*parameters.map { "" }.toTypedArray())
        }
    }
}
//object TypeUtil {
//    val qrCodeTypes: List<QrCodeType> = QrCodeType::class.sealedSubclasses.map { clazz ->
//        clazz.objectInstance ?: clazz.constructors.first().run {
//            call(*parameters.map { "" }.toTypedArray())
//            // call(*parameters.map { it.type.toString() }.toTypedArray())
//        }
//    }
//}
