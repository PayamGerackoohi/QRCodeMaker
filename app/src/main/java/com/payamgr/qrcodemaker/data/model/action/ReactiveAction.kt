package com.payamgr.qrcodemaker.data.model.action

data class ReactiveAction<T>(val data: T, val onDataChanged: (T) -> Unit)

fun ReactiveAction<Boolean>.toggle() = onDataChanged(data.not())

fun ReactiveAction<Boolean>.clear() = onDataChanged(false)
