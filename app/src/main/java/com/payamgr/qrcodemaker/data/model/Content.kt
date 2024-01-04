package com.payamgr.qrcodemaker.data.model

abstract class Content {
    abstract var id: Int
    abstract val title: String
    abstract val qrCodeType: QrCodeType
    abstract fun string(): String

    companion object {
        fun Map<InputId, InputData>.unwrap(id: InputId, defaultValue: String = "") =
            this[id]?.value ?: defaultValue
    }
}
