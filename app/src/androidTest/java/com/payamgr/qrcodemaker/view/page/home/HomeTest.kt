package com.payamgr.qrcodemaker.view.page.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.LargeTest
import com.airbnb.mvrx.mocking.MockableMavericks
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.model.event.HomeEffect
import com.payamgr.qrcodemaker.data.model.state.HomeState
import com.payamgr.qrcodemaker.test_util.ActivityTest
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.Screenshot
import com.payamgr.qrcodemaker.test_util.StringId
import com.payamgr.qrcodemaker.test_util.app
import com.payamgr.qrcodemaker.test_util.take
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@LargeTest
class HomeTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun page_noContent_Test() {
        MockableMavericks.initialize(app)
        val viewModel = object : HomeVM(HomeState()) {
            override val effect = flow<HomeEffect> {}
            override fun showQrCode(content: Content) {}
            override fun showContentTypePage() {}
        }

        rule.setContent {
            Home.Page(
                navigateToContentType = {},
                navigateToShowQrCode = {},
                viewModel = viewModel,
            )
        }

        // Verify the initial state
        // - Verify the empty list
        rule.onNodeWithTag("Qr-Code Contents")
            .assertIsDisplayed()
            .onChildren()
            .assertCountEquals(0)

        // Verify the 'Add Content' button is displayed
        rule.onNodeWithContentDescription("Add Content").assertIsDisplayed()
    }

    @Test
    fun page_AddContent_Test() {
        MockableMavericks.initialize(app)
        val showContentTypePage = mockk<() -> Unit>()
        justRun { showContentTypePage() }

        val viewModel = object : HomeVM(HomeState()) {
            override val effect = flow<HomeEffect> {}
            override fun showQrCode(content: Content) {}
            override fun showContentTypePage() = showContentTypePage()
        }

        rule.setContent {
            Home.Page(
                navigateToContentType = {},
                navigateToShowQrCode = {},
                viewModel = viewModel,
            )
        }

        // Verify the button is displayed
        rule.onNodeWithContentDescription("Add Content").assertIsDisplayed()

            // Verify click
            .performClick()
        verify { showContentTypePage() }

        confirmVerified()
    }

    @Test
    fun page_HandleEffects_Test() = runTest {
        MockableMavericks.initialize(app)
        val navigateToContentType = mockk<() -> Unit>()
        val navigateToShowQrCode = mockk<(Content) -> Unit>()
        justRun { navigateToContentType() }
        justRun { navigateToShowQrCode(any()) }

        val viewModel = object : HomeVM(HomeState()) {
            val effectChannel = Channel<HomeEffect>()
            override val effect = effectChannel.receiveAsFlow()
            override fun showQrCode(content: Content) {}
            override fun showContentTypePage() {}
        }

        rule.setContent {
            Home.Page(
                navigateToContentType = navigateToContentType,
                navigateToShowQrCode = navigateToShowQrCode,
                viewModel = viewModel,
            )
        }

        // Verify 'NavigateToContentType'
        viewModel.effectChannel.send(HomeEffect.NavigateToContentType)
        verify { navigateToContentType() }

        // Verify 'NavigateToShowQrCode'
        viewModel.effectChannel.send(HomeEffect.NavigateToShowQrCode(Fake.Content.text))
        verify { navigateToShowQrCode(Fake.Content.text) }

        confirmVerified()
    }

    @Test
    fun addContentButtonTest() {
        val onClick = mockk<() -> Unit>()
        justRun { onClick() }

        rule.setContent {
            Home.AddContentButton(onClick = onClick)
        }

        // Verify initial state
        rule.onNodeWithContentDescription("Add Content").assertIsDisplayed()

            // Verify click
            .performClick()
        verify { onClick() }

        confirmVerified()
    }

    @Test
    fun qrCodeContentTest() {
        val onClick = mockk<() -> Unit>()
        justRun { onClick() }

        rule.setContent {
            Home.QrCodeContent(
                content = Fake.Content.text,
                onClick = onClick,
            )
        }

        // Verify initial state
        rule.onNodeWithTag("Home.QrCodeContent")
            .assertIsDisplayed()
            .assertTextEquals("Sample Text", "Text")

            // Verify click
            .performClick()
        verify { onClick() }

        confirmVerified()
    }
}

@LargeTest
class HomeActivityTest : ActivityTest() {
    @Test
    fun page_someContent_Test() {
        MockableMavericks.initialize(app)

        val textTitle = StringId(QrCodeType.Text().titleId)
        val phoneCallTitle = StringId(QrCodeType.PhoneCall().titleId)
        val meCardTitle = StringId(QrCodeType.MeCard().titleId)

        val showQrCode = mockk<(Content) -> Unit>()
        justRun { showQrCode(any()) }

        val state = HomeState(Fake.Content.run { listOf(text, phone, meCard) })

        val viewModel = object : HomeVM(state) {
            override val effect = flow<HomeEffect> {}
            override fun showQrCode(content: Content) = showQrCode(content)
            override fun showContentTypePage() {}
        }

        rule.setContent {
            QRCodeMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home.Page(
                        navigateToContentType = {},
                        navigateToShowQrCode = {},
                        viewModel = viewModel,
                    )
                }
            }
        }

        // Verify the initial state
        // - Verify the list is displayed
        rule.onNodeWithTag("Qr-Code Contents")
            .assertIsDisplayed()
            .onChildren().apply {
                assertCountEquals(3)

                // -- Verify the 'Text' content
                this[0]
                    .assertIsDisplayed()
                    .assert(hasTestTag("Home.QrCodeContent"))
                    .assertTextEquals("Sample Text", textTitle.label)

                // -- Verify the 'PhoneCall' content
                this[1]
                    .assertIsDisplayed()
                    .assert(hasTestTag("Home.QrCodeContent"))
                    .assertTextEquals("Sample Phone Call", phoneCallTitle.label)

                // -- Verify the 'Me-Card' content
                this[2]
                    .assertIsDisplayed()
                    .assert(hasTestTag("Home.QrCodeContent"))
                    .assertTextEquals("Sample Me-Card", meCardTitle.label)
            }

        Screenshot.Home.take()

        // Verify item click
        // - Text item
        // -- Click
        rule.onNodeWithText(textTitle.label).performClick()

        // -- Verify viewModel::showQrCode is called
        verify { showQrCode(Fake.Content.text) }

        // - PhoneCall item
        // -- Click
        rule.onNodeWithText(phoneCallTitle.label).performClick()

        // -- Verify viewModel::showQrCode is called
        verify { showQrCode(Fake.Content.phone) }

        // - Me-Card item
        // -- Click
        rule.onNodeWithText(meCardTitle.label).performClick()

        // -- Verify viewModel::showQrCode is called
        verify { showQrCode(Fake.Content.meCard) }

        // Verify the 'Add Content' button is displayed
        rule.onNodeWithContentDescription("Add Content").assertIsDisplayed()

        confirmVerified()
    }
}
