package com.payamgr.qrcodemaker.data.model

object TypeUtil {
    // todo flaky reflection: naively assumes that all the constructors receive string parameters
    val qrCodeTypes: List<QrCodeType> = QrCodeType::class.sealedSubclasses.map { clazz ->
        clazz.objectInstance ?: clazz.constructors.first().run {
            call(*parameters.map { "" }.toTypedArray())
            // call(*parameters.map { it.type.toString() }.toTypedArray())
        }
    }
}
