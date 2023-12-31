package com.payamgr.qrcodemaker.view.page.content_form

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.event.ContentFormEvent
import com.payamgr.qrcodemaker.data.model.state.ContentFormState
import com.payamgr.qrcodemaker.data.repository.ContentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ContentFormVMImpl @AssistedInject constructor(
    @Assisted initialState: ContentFormState,
    private val repository: ContentRepository,
) : ContentFormVM(initialState) {
    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ContentFormVMImpl, ContentFormState>

    private val event = Channel<ContentFormEvent>()
    override val eventFlow = event.receiveAsFlow()

    init {
        observeCurrentQrCodeType()
        loadContentTitle()
    }

    private fun loadContentTitle() = viewModelScope.launch {
        repository.currentContent.collectLatest {
            it?.let { content ->
                setState { copy(currentContent = content, contentTitle = content.title) }
            }
        }
    }

    private fun observeCurrentQrCodeType() = viewModelScope.launch {
        repository.currentQrCodeType.collectLatest {
            setState { copy(currentQrCodeType = it) }
        }
    }

    override fun add(content: Content) = viewModelScope.launch {
        repository.add(content)
        event.send(ContentFormEvent.ClosePage)
    }

    override fun update(content: Content) = withState {
        it.currentContent?.id?.let { id ->
            viewModelScope.launch {
                content.id = id
                repository.update(content)
                event.send(ContentFormEvent.ClosePage)
            }
        }
    }
}
