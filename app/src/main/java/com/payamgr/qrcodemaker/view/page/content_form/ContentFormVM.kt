package com.payamgr.qrcodemaker.view.page.content_form

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.event.ContentFormEvent
import com.payamgr.qrcodemaker.data.model.state.ContentFormState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

abstract class ContentFormVM(initialState: ContentFormState) : MavericksViewModel<ContentFormState>(initialState) {
    companion object :
        MavericksViewModelFactory<ContentFormVM, ContentFormState> by hiltMavericksViewModelFactory()

    abstract val eventFlow: Flow<ContentFormEvent>
    abstract fun add(content: Content): Any?
    abstract fun update(content: Content): Any?
}
