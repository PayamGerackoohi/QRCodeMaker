package com.payamgr.qrcodemaker.data.model.event

sealed class ShowQrCodeEvent {
    object ClosePage : ShowQrCodeEvent()
    object NavigateToContentForm : ShowQrCodeEvent()
}
