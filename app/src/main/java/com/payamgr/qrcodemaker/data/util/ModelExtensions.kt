package com.payamgr.qrcodemaker.data.util

import com.payamgr.qrcodemaker.data.model.Input
import com.payamgr.qrcodemaker.data.model.InputData

fun Input.string(): String = when (this) {
    is Input.Single -> "Single(id: $id, initialValue: $initialValue)"
    is Input.Group -> "Group(inputs: ${inputs.map { it.string() }})"
}

val InputData.isValid: Boolean get() = isOptional || value.isNotBlank()
