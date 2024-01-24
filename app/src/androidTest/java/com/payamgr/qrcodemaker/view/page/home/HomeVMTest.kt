package com.payamgr.qrcodemaker.view.page.home

import androidx.test.filters.MediumTest
import com.airbnb.mvrx.test.MavericksTestRule
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.event.HomeEffect
import com.payamgr.qrcodemaker.data.model.state.HomeState
import com.payamgr.qrcodemaker.data.repository.ContentRepository
import com.payamgr.qrcodemaker.test_util.Fake
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.*
import org.junit.Rule
import org.junit.Test

@MediumTest
class HomeVMTest {
    @get:Rule
    val mavericksTestRule = MavericksTestRule()

    @Test
    fun initialState_noContent_verification() = runTest {
        val repository = mockk<ContentRepository>()
        coEvery { repository.loadContents() } returns MutableStateFlow(listOf())
        val viewModel = HomeVMImpl(HomeState(), repository)

        // Verify the initial state
        assertThat(viewModel.awaitState().contents).isEqualTo(listOf<Content>())
        coVerify { repository.loadContents() }

        confirmVerified()
    }

    @Test
    fun initialState_someContent_verification() = runTest {
        val contents = listOf(Fake.Content.text, Fake.Content.phone)
        val repository = mockk<ContentRepository>()
        coEvery { repository.loadContents() } returns MutableStateFlow(contents)
        val viewModel = HomeVMImpl(HomeState(), repository)

        // Verify the initial state
        assertThat(viewModel.awaitState().contents).isEqualTo(contents)
        coVerify { repository.loadContents() }

        confirmVerified()
    }

    @Test
    fun showQrCode_test() = runTest {
        val repository = mockk<ContentRepository>()
        coEvery { repository.loadContents() } returns MutableStateFlow(listOf(Fake.Content.text))
        coJustRun { repository.push(any(Content::class)) }

        val viewModel = HomeVMImpl(HomeState(), repository)

        // Verify initial state
        assertThat(viewModel.awaitState()).isEqualTo(HomeState(listOf(Fake.Content.text)))
        coVerify { repository.loadContents() }

        // Call 'showQrCode'
        val content = Fake.Content.text
        viewModel.showQrCode(content)

        // Verify repository::push is called
        coVerify { repository.push(content) }

        // Verify the HomeEffect.NavigateToShowQrCode effect is triggered
        assertThat(viewModel.effect.first()).isEqualTo(HomeEffect.NavigateToShowQrCode(content))

        confirmVerified()
    }

    @Test
    fun showContentTypePage_test() = runTest {
        val repository = mockk<ContentRepository>()
        coEvery { repository.loadContents() } returns MutableStateFlow(listOf())

        val viewModel = HomeVMImpl(HomeState(), repository)

        // Verify initial state
        assertThat(viewModel.awaitState()).isEqualTo(HomeState(listOf()))
        coVerify { repository.loadContents() }

        // Call 'showContentTypePage'
        viewModel.showContentTypePage()

        // Verify the HomeEffect.NavigateToContentType effect is triggered
        assertThat(viewModel.effect.first()).isEqualTo(HomeEffect.NavigateToContentType)

        confirmVerified()
    }
}
