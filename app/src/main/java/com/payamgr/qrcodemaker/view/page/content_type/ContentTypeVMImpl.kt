package com.payamgr.qrcodemaker.view.page.content_type

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.util.TypeUtil
import com.payamgr.qrcodemaker.data.model.event.ContentTypeEffect
import com.payamgr.qrcodemaker.data.model.state.ContentTypeState
import com.payamgr.qrcodemaker.data.repository.ContentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ContentTypeVMImpl @AssistedInject constructor(
    @Assisted initialState: ContentTypeState,
    private val repository: ContentRepository,
) : ContentTypeVM(initialState) {
    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ContentTypeVMImpl, ContentTypeState>

    private val _effect = Channel<ContentTypeEffect>()
    override val effect = _effect.receiveAsFlow()

    init {
        loadQrCodeTypes()
    }

    private fun loadQrCodeTypes() {
        setState { copy(qrCodeTypes = TypeUtil.qrCodeTypes) }
    }

    override fun showContentForm(type: QrCodeType) = viewModelScope.launch {
        repository.push(type)
        _effect.send(ContentTypeEffect.NavigateToContentForm)
    }
}
