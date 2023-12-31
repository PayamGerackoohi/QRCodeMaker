package com.payamgr.qrcodemaker.data.model

import androidx.annotation.StringRes
import androidx.compose.ui.text.input.KeyboardType

sealed class Input {
    data class Single(
        val id: InputId,
        @StringRes val labelId: Int,
        val initialValue: String,
        val keyboardType: KeyboardType,
        val isOptional: Boolean = false,
        val singleLine: Boolean = true,
    ) : Input()

    data class Group(@StringRes val titleId: Int, val inputs: List<Single>) : Input()
}

enum class InputId {
    Title,
    Text,
    Phone,
    FirstName,
    LastName,
}
// NamePrefix,
// MiddleName,
// NamePostfix,
