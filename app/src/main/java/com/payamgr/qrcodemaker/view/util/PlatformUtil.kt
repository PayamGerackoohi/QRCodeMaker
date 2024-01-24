package com.payamgr.qrcodemaker.view.util

import androidx.compose.runtime.MutableState
import com.payamgr.qrcodemaker.data.model.action.ReactiveAction

fun <T> actionOf(state: MutableState<T>): ReactiveAction<T> {
    return ReactiveAction(
        data = state.value,
        onDataChanged = { state.value = it }
    )
}
