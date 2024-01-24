package com.payamgr.qrcodemaker.view.page.show_qrcode

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.data.model.event.ShowQrCodeEvent
import com.payamgr.qrcodemaker.data.model.state.ShowQrCodeState
import kotlinx.coroutines.flow.Flow

abstract class ShowQrCodeVM(initialState: ShowQrCodeState) : MavericksViewModel<ShowQrCodeState>(initialState) {
    companion object : MavericksViewModelFactory<ShowQrCodeVM, ShowQrCodeState> by hiltMavericksViewModelFactory()

    abstract val effect: Flow<ShowQrCodeEvent>
    abstract fun removeContent(): Any?
    abstract fun editContent(): Any?
    abstract fun onEccChanged(ecc: ErrorCorrectionCodeLevel)
}
