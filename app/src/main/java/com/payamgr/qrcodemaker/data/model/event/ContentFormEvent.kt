package com.payamgr.qrcodemaker.data.model.event

sealed class ContentFormEvent {
    object ClosePage : ContentFormEvent()
}
