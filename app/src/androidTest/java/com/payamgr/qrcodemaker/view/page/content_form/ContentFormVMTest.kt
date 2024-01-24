package com.payamgr.qrcodemaker.view.page.content_form

import androidx.test.filters.MediumTest
import com.airbnb.mvrx.mocking.MockableMavericks
import com.airbnb.mvrx.test.MavericksTestRule
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.model.event.ContentFormEffect
import com.payamgr.qrcodemaker.data.model.state.ContentFormState
import com.payamgr.qrcodemaker.data.repository.ContentRepository
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.app
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.*
import org.junit.Rule
import org.junit.Test

@MediumTest
class ContentFormVMTest {
    @get:Rule
    val mavericksTestRule = MavericksTestRule()

    @Test
    fun add_test() = runTest {
        val (viewModel, repository, currentQrCodeType) = setupViewModel(
            stub = { repository ->
                coJustRun { repository.add(any()) }
            }
        )

        // Verify initial state
        // - Verify the current states are loaded from the repository
        verify { repository.currentQrCodeType }
        verify { repository.currentContent }

        // - Verify the state
        assertThat(viewModel.awaitState()).isEqualTo(
            ContentFormState(
                currentQrCodeType = currentQrCodeType,
                currentContent = Fake.Content.text,
                contentTitle = Fake.Content.text.title,
            )
        )

        // Verify 'add'
        viewModel.add(Fake.Content.text)

        // - Verify the repository update is called
        coVerify { repository.add(Fake.Content.text) }

        // - Verify the 'ClosePage' effect is triggered
        assertThat(viewModel.effect.first()).isEqualTo(ContentFormEffect.ClosePage)

        confirmVerified()
    }

    @Test
    fun update_test() = runTest {
        val (viewModel, repository, currentQrCodeType, oldContent) = setupViewModel(
            stub = { repository -> coJustRun { repository.update(any()) } },
            setupState = { _, content -> content.id = 123 },
        )

        // Verify initial state
        // - Verify the current states are loaded from the repository
        verify { repository.currentQrCodeType }
        verify { repository.currentContent }

        // - Verify the state
        assertThat(viewModel.awaitState()).isEqualTo(
            ContentFormState(
                currentQrCodeType = currentQrCodeType,
                currentContent = oldContent,
                contentTitle = oldContent.title,
            )
        )

        // Verify 'update'
        // - Verify the new content id is not set
        val newContent = Fake.Content.phone
        assertThat(newContent.id).isEqualTo(0)

        // - Commit 'update'
        viewModel.update(newContent)

        // - Verify the new content id is set
        assertThat(newContent.id).isEqualTo(123)

        // - Verify the repository update is called
        coVerify { repository.update(newContent) }

        // - Verify the 'ClosePage' effect is triggered
        assertThat(viewModel.effect.first()).isEqualTo(ContentFormEffect.ClosePage)

        confirmVerified()
    }


    private data class SetupResult(
        val viewModel: ContentFormVM,
        val repository: ContentRepository,
        val currentQrCodeType: QrCodeType,
        val currentContent: Content,
    )

    private fun setupViewModel(
        stub: (ContentRepository) -> Unit,
        setupState: (QrCodeType, Content) -> Unit = { _, _ -> },
    ): SetupResult {
        MockableMavericks.initialize(app)
        val repository = mockk<ContentRepository>()
        stub(repository)
        val currentQrCodeType = QrCodeType.Text()
        val currentContent = Fake.Content.text
        setupState(currentQrCodeType, currentContent)
        every { repository.currentQrCodeType } returns MutableStateFlow(currentQrCodeType)
        every { repository.currentContent } returns MutableStateFlow(currentContent)
        val viewModel = ContentFormVMImpl(ContentFormState(), repository)
        return SetupResult(viewModel, repository, currentQrCodeType, currentContent)
    }
}
