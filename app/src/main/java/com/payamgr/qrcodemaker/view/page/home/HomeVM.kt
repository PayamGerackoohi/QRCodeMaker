package com.payamgr.qrcodemaker.view.page.home

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.event.HomeEffect
import com.payamgr.qrcodemaker.data.model.state.HomeState
import kotlinx.coroutines.flow.Flow

abstract class HomeVM(initialState: HomeState) : MavericksViewModel<HomeState>(initialState) {
    companion object : MavericksViewModelFactory<HomeVM, HomeState> by hiltMavericksViewModelFactory()

    abstract val effect: Flow<HomeEffect>
    abstract fun showQrCode(content: Content): Any?
    abstract fun showContentTypePage(): Any?
}
