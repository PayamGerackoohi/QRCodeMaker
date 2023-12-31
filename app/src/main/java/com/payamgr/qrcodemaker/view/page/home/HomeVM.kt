package com.payamgr.qrcodemaker.view.page.home

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.event.HomeEvent
import com.payamgr.qrcodemaker.data.model.state.HomeState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

abstract class HomeVM(initialState: HomeState) : MavericksViewModel<HomeState>(initialState) {
    companion object : MavericksViewModelFactory<HomeVM, HomeState> by hiltMavericksViewModelFactory()

    abstract val eventFlow: Flow<HomeEvent>
    abstract fun showQrCode(content: Content): Job
    abstract fun showContentTypePage(): Job
}
