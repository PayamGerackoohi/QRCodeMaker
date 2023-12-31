package com.payamgr.qrcodemaker.data.model

data class InputData(val value: String, val isOptional: Boolean) {
    val isValid: Boolean get() = isOptional || value.isNotBlank()
}
