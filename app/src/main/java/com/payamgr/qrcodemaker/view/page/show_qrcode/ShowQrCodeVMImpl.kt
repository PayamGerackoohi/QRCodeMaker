package com.payamgr.qrcodemaker.view.page.show_qrcode

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.data.model.event.ShowQrCodeEvent
import com.payamgr.qrcodemaker.data.model.state.ShowQrCodeState
import com.payamgr.qrcodemaker.data.repository.ContentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ShowQrCodeVMImpl @AssistedInject constructor(
    @Assisted initialState: ShowQrCodeState,
    private val repository: ContentRepository,
) : ShowQrCodeVM(initialState) {
    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ShowQrCodeVMImpl, ShowQrCodeState>

    private val event = Channel<ShowQrCodeEvent>()
    override val eventFlow = event.receiveAsFlow()

    init {
        observeCurrentContent()
        observeEcc()
    }

    private fun observeEcc() {
        onEach(ShowQrCodeState::ecc, ShowQrCodeState::currentContent) { ecc, content ->
            delay(100)
            content?.let { c ->
                viewModelScope.launch {
                    val qrCode = c.qrCode(ecc)
                    setState { copy(qrCode = qrCode, ecc = ecc) }
                }
            }
        }
    }

    private fun observeCurrentContent() = viewModelScope.launch {
        repository.currentContent.collectLatest { content ->
            setState { copy(currentContent = content) }
            content?.let { c ->
                withState { state ->
                    viewModelScope.launch {
                        val qrCode = c.qrCode(state.ecc)
                        setState { copy(qrCode = qrCode) }
                    }
                }
            }
        }
    }

    override fun removeContent() = viewModelScope.launch {
        repository.removeCurrentContent()
        event.send(ShowQrCodeEvent.ClosePage)
    }

    override fun editContent() = withState {
        it.currentContent?.let { content ->
            viewModelScope.launch {
                repository.push(content.qrCodeType)
                event.send(ShowQrCodeEvent.NavigateToContentForm)
            }
        }
    }

    override fun onEccChanged(ecc: ErrorCorrectionCodeLevel) = setState { copy(ecc = ecc) }
}
