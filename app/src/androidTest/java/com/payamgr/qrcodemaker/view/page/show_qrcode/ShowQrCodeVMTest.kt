package com.payamgr.qrcodemaker.view.page.show_qrcode

import androidx.test.filters.MediumTest
import com.airbnb.mvrx.test.MavericksTestRule
import com.payamgr.qrcodemaker.data.QrCodeMaker
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.model.event.ShowQrCodeEvent
import com.payamgr.qrcodemaker.data.model.state.ShowQrCodeState
import com.payamgr.qrcodemaker.data.repository.ContentRepository
import com.payamgr.qrcodemaker.test_util.Fake
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.*
import org.junit.Rule
import org.junit.Test

typealias Stub = (ContentRepository, QrCodeMaker) -> Unit

@MediumTest
class ShowQrCodeVMTest {
    @get:Rule
    val mavericksTestRule = MavericksTestRule()

    @Test
    fun removeContent_repositoryNoContent_test() = runTest {
        val (viewModel, repository, qrCodeMaker) = setupNoContentViewModel { repository, _ ->
            coJustRun { repository.removeCurrentContent() }
        }

        // Verify the initial state
        verify { repository.currentContent }
        assertThat(viewModel.awaitState()).isEqualTo(ShowQrCodeState())

        // Remove the content
        viewModel.removeContent()

        // Verify the repository::removeCurrentContent is called
        coVerify { repository.removeCurrentContent() }

        // Verify the 'ShowQrCodeEvent.ClosePage' effect is triggered
        assertThat(viewModel.effect.first()).isEqualTo(ShowQrCodeEvent.ClosePage)

        // Verify the qrCodeMaker::encode is not called
        coVerify(exactly = 0) { qrCodeMaker.encode(any(), any()) }

        // todo confirmVerified is not working!
        // confirmVerified()
    }

    @Test
    fun removeContent_repositorySomeContent_test() = runTest {
        val (viewModel, repository, qrCodeMaker) = setupSomeContentViewModel { repository, _ ->
            coJustRun { repository.removeCurrentContent() }
        }

        // Verify the initial state
        // - Verify the repository::currentContent is loaded
        verify { repository.currentContent }

        // - Verify the qrCodeMaker::encode is called
        coVerify { qrCodeMaker.encode(Fake.Content.text, ErrorCorrectionCodeLevel.Medium) }

        // - Verify the viewModel::state is updated
        assertThat(viewModel.awaitState())
            .isEqualTo(
                ShowQrCodeState(
                    currentContent = Fake.Content.text,
                    qrCode = Fake.Data.QR_CODE_2x2,
                )
            )

        // Remove the content
        viewModel.removeContent()

        // Verify the repository::removeCurrentContent is called
        coVerify { repository.removeCurrentContent() }

        // Verify the 'ShowQrCodeEvent.ClosePage' effect is triggered
        assertThat(viewModel.effect.first()).isEqualTo(ShowQrCodeEvent.ClosePage)

        // - Verify the qrCodeMaker::encode is called just once
        coVerify(exactly = 1) { qrCodeMaker.encode(Fake.Content.text, ErrorCorrectionCodeLevel.Medium) }

        // todo confirmVerified is not working!
//        confirmVerified()
    }

    @Test
    fun editContent_test() = runTest {
        val (viewModel, repository, qrCodeMaker) = setupSomeContentViewModel { repository, _ ->
            coJustRun { repository.push(any(QrCodeType::class)) }
        }

        // Verify the initial state
        // - Verify the repository::currentContent is loaded
        verify { repository.currentContent }

        // - Verify the qrCodeMaker::encode is called
        coVerify { qrCodeMaker.encode(Fake.Content.text, ErrorCorrectionCodeLevel.Medium) }

        // - Verify the viewModel::state is updated
        assertThat(viewModel.awaitState())
            .isEqualTo(
                ShowQrCodeState(
                    currentContent = Fake.Content.text,
                    qrCode = Fake.Data.QR_CODE_2x2,
                )
            )

        // Remove the content
        viewModel.editContent()

        // Verify the repository::push is called
        coVerify { repository.push(Fake.Content.text.qrCodeType) }

        // Verify the 'ShowQrCodeEvent.NavigateToContentForm' effect is triggered
        assertThat(viewModel.effect.first()).isEqualTo(ShowQrCodeEvent.NavigateToContentForm)

        // - Verify the qrCodeMaker::encode is called just once
        coVerify(exactly = 1) { qrCodeMaker.encode(Fake.Content.text, ErrorCorrectionCodeLevel.Medium) }

        // todo confirmVerified is not working!
        // confirmVerified()
    }

    @Test
    fun onEccChanged_test() = runTest {
        val (viewModel, repository, qrCodeMaker) = setupSomeContentViewModel()

        // Verify the initial state
        // - Verify the repository::currentContent is loaded
        verify { repository.currentContent }

        // - Verify the qrCodeMaker::encode is called
        coVerify { qrCodeMaker.encode(Fake.Content.text, ErrorCorrectionCodeLevel.Medium) }

        // - Verify the viewModel::state is updated
        assertThat(viewModel.awaitState())
            .isEqualTo(
                ShowQrCodeState(
                    currentContent = Fake.Content.text,
                    qrCode = Fake.Data.QR_CODE_2x2,
                )
            )

        // Remove the content
        viewModel.onEccChanged(ErrorCorrectionCodeLevel.High)

        // - Verify the viewModel::state is updated
        assertThat(viewModel.awaitState()).isEqualTo(
            ShowQrCodeState(
                currentContent = Fake.Content.text,
                qrCode = Fake.Data.QR_CODE_2x2,
                ecc = ErrorCorrectionCodeLevel.High,
            )
        )

        // - Verify the qrCodeMaker::encode is called just once
        coVerify(exactly = 1) { qrCodeMaker.encode(Fake.Content.text, ErrorCorrectionCodeLevel.Medium) }

        // todo confirmVerified is not working!
        // confirmVerified()
    }

    private data class ViewModelSetupResult(
        val viewModel: ShowQrCodeVM,
        val repository: ContentRepository,
        val qrCodeMaker: QrCodeMaker,
    )

    private fun setupNoContentViewModel(stub: Stub = { _, _ -> }): ViewModelSetupResult =
        setupViewModel(null, stub)

    private fun setupSomeContentViewModel(stub: Stub = { _, _ -> }): ViewModelSetupResult =
        setupViewModel(Fake.Content.text, stub)

    private fun setupViewModel(content: Content?, stub: Stub): ViewModelSetupResult {
        val repository = mockk<ContentRepository>()
        val qrCodeMaker = mockk<QrCodeMaker>()
        every { repository.currentContent } returns MutableStateFlow(content)
        coEvery { qrCodeMaker.encode(any(), any()) } returns Fake.Data.QR_CODE_2x2
        stub(repository, qrCodeMaker)
        val viewModel = ShowQrCodeVMImpl(
            initialState = ShowQrCodeState(),
            repository = repository,
            qrCodeMaker = qrCodeMaker,
        )
        return ViewModelSetupResult(viewModel, repository, qrCodeMaker)
    }
}
