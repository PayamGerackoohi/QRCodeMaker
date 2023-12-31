package com.payamgr.qrcodemaker.view.page.home

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.event.HomeEvent
import com.payamgr.qrcodemaker.data.model.state.HomeState
import com.payamgr.qrcodemaker.data.repository.ContentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeVMImpl @AssistedInject constructor(
    @Assisted initialState: HomeState,
    private val repository: ContentRepository,
) : HomeVM(initialState) {
    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeVMImpl, HomeState>

    private val event = Channel<HomeEvent>()
    override val eventFlow = event.receiveAsFlow()

    init {
        observeContents()
    }

    private fun observeContents() = viewModelScope.launch {
        repository.loadContents().collectLatest { contents ->
            setState { copy(contents = contents) }
        }
    }

    override fun showQrCode(content: Content) = viewModelScope.launch {
        repository.push(content)
        event.send(HomeEvent.NavigateToShowQrCode(content))
    }

    override fun showContentTypePage() = viewModelScope.launch {
        event.send(HomeEvent.NavigateToContentType)
    }
}
