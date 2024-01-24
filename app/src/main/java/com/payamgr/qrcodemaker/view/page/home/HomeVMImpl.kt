package com.payamgr.qrcodemaker.view.page.home

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.event.HomeEffect
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

    private val _effect = Channel<HomeEffect>()
    override val effect = _effect.receiveAsFlow()

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
        _effect.send(HomeEffect.NavigateToShowQrCode(content))
    }

    override fun showContentTypePage() = viewModelScope.launch {
        _effect.send(HomeEffect.NavigateToContentType)
    }
}
