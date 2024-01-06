package com.payamgr.qrcodemaker.view.page.content_type

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.model.event.ContentTypeEvent
import com.payamgr.qrcodemaker.data.model.state.ContentTypeState
import kotlinx.coroutines.flow.Flow

abstract class ContentTypeVM(initialState: ContentTypeState) : MavericksViewModel<ContentTypeState>(initialState) {
    companion object :
        MavericksViewModelFactory<ContentTypeVM, ContentTypeState> by hiltMavericksViewModelFactory()

    abstract val eventFlow: Flow<ContentTypeEvent>
    abstract fun showContentForm(type: QrCodeType): Any?
}
