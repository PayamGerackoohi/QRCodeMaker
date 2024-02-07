package com.payamgr.qrcodemaker.view.page.content_type

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.LargeTest
import com.airbnb.mvrx.mocking.MockableMavericks
import com.payamgr.qrcodemaker.R
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.model.event.ContentTypeEffect
import com.payamgr.qrcodemaker.data.model.state.ContentTypeState
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@LargeTest
class ContentTypeTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun pageTest() = runTest {
        MockableMavericks.initialize(app)

        val qrCodeTypes = listOf(QrCodeType.Text(), QrCodeType.PhoneCall(), QrCodeType.MeCard())
        val state = ContentTypeState(qrCodeTypes = qrCodeTypes)
        val (textTitle, phoneCallTitle, meCardTitle) = qrCodeTypes.map { StringId(it.titleId) }

        val showContentForm = mockk<(QrCodeType) -> Unit>()
        val navigateToContentForm = mockk<() -> Unit>()
        justRun { showContentForm(any()) }
        justRun { navigateToContentForm() }

        val viewModel = object : ContentTypeVM(state) {
            val effectChannel = Channel<ContentTypeEffect>()
            override val effect = effectChannel.receiveAsFlow()
            override fun showContentForm(type: QrCodeType) = showContentForm(type)
        }

        rule.setContent {
            QRCodeMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContentType.Page(
                        viewModel = viewModel,
                        navigateToContentForm = navigateToContentForm,
                    )
                }
            }
        }

        Screenshot.ContentType.take()

        // Verify initial state
        // - Verify the list is displayed
        rule.onNodeWithTag("QR-Code Types")
            .assertIsDisplayed()
            .onChildren().apply {
                assertCountEquals(3)
                // - Verify QrCodeTypeItem: Text
                this[0]
                    .assertIsDisplayed()
                    .assertContentDescriptionEquals("Qr-Code Type Item")
                    .assertTextEquals(textTitle.label)

                // - Verify QrCodeTypeItem: PhoneCall
                this[1]
                    .assertIsDisplayed()
                    .assertContentDescriptionEquals("Qr-Code Type Item")
                    .assertTextEquals(phoneCallTitle.label)

                // - Verify QrCodeTypeItem: Me-Card
                this[2]
                    .assertIsDisplayed()
                    .assertContentDescriptionEquals("Qr-Code Type Item")
                    .assertTextEquals(meCardTitle.label)
            }

        // Verify actions
        // - QrCodeType.Text
        // -- Click
        rule.onNodeWithText(textTitle.label).performClick()

        // -- Verify 'Text' actions
        verify { showContentForm(qrCodeTypes[0]) }

        // - QrCodeType.PhoneCall
        // -- Click
        rule.onNodeWithText(phoneCallTitle.label).performClick()

        // -- Verify 'PhoneCall' actions
        verify { showContentForm(qrCodeTypes[1]) }

        // - QrCodeType.MeCard
        // -- Click
        rule.onNodeWithText(meCardTitle.label).performClick()

        // -- Verify 'Me-Card' actions
        verify { showContentForm(qrCodeTypes[2]) }

        // Verify Handle Effects
        viewModel.effectChannel.send(ContentTypeEffect.NavigateToContentForm)
        verify { navigateToContentForm() }

        confirmVerified()
    }

    @Test
    fun qrCodeTypeItemTest() {
        val title = StringId(R.string.title)
        val onItemClicked = mockk<() -> Unit>()
        justRun { onItemClicked() }

        rule.setContent {
            ContentType.QrCodeTypeItem(
                titleId = title.resource,
                onItemClicked = onItemClicked,
            )
        }

        // Verify initial state
        rule.onNodeWithText(title.label)
            .assertIsDisplayed()
            .assertContentDescriptionEquals("Qr-Code Type Item")

            // Verify click
            .performClick()
        verify { onItemClicked() }

        confirmVerified()

    }
}
