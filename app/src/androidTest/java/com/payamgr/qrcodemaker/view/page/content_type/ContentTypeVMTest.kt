package com.payamgr.qrcodemaker.view.page.content_type

import androidx.test.filters.MediumTest
import com.airbnb.mvrx.test.MavericksTestRule
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.model.event.ContentTypeEffect
import com.payamgr.qrcodemaker.data.model.state.ContentTypeState
import com.payamgr.qrcodemaker.data.repository.ContentRepository
import com.payamgr.qrcodemaker.data.util.TypeUtil
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.*
import org.junit.Rule
import org.junit.Test

@MediumTest
class ContentTypeVMTest {
    @get:Rule
    val mavericksTestRule = MavericksTestRule()

    @Test
    fun initialState_verification() = runTest {
        val viewModel = ContentTypeVMImpl(
            initialState = ContentTypeState(),
            repository = mockk(),
        )

        // verify Qr-Code types are loaded
        viewModel.awaitState().apply {
            assertThat(qrCodeTypes).isEqualTo(TypeUtil.qrCodeTypes)
        }
    }

    @Test
    fun showContentFormTest() = runTest {
        val repository = mockk<ContentRepository>()
        coJustRun { repository.push(any(QrCodeType::class)) }

        val viewModel = ContentTypeVMImpl(
            initialState = ContentTypeState(),
            repository = repository,
        )

        // call 'showContentForm'
        val targetType = QrCodeType.Text()
        viewModel.showContentForm(targetType)

        // - Verify the repository 'push' is called
        coVerify { repository.push(targetType) }

        // - Verify the effect is triggered
        assertThat(viewModel.effect.first()).isEqualTo(ContentTypeEffect.NavigateToContentForm)

        confirmVerified()
    }
}
