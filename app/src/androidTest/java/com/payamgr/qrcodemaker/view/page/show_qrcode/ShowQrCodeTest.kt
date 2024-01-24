package com.payamgr.qrcodemaker.view.page.show_qrcode

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.LargeTest
import com.airbnb.mvrx.mocking.MockableMavericks
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.data.model.event.ShowQrCodeEvent
import com.payamgr.qrcodemaker.data.model.state.ShowQrCodeState
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.Screenshot
import com.payamgr.qrcodemaker.test_util.app
import com.payamgr.qrcodemaker.test_util.assertHasRole
import com.payamgr.qrcodemaker.test_util.requestLandscapeOrientation
import com.payamgr.qrcodemaker.test_util.requestPortraitOrientation
import com.payamgr.qrcodemaker.test_util.take
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import com.payamgr.qrcodemaker.view.util.actionOf
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

@LargeTest
class ShowQrCodeTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun page_invalidContent_test() {
        rule.requestPortraitOrientation()

        MockableMavericks.initialize(app)

        val viewModel = object : ShowQrCodeVM(ShowQrCodeState()) {
            override val effect = flow<ShowQrCodeEvent> {}
            override fun removeContent() {}
            override fun editContent() {}
            override fun onEccChanged(ecc: ErrorCorrectionCodeLevel) {}
        }

        rule.setContent {
            ShowQrCode.Page(
                viewModel = viewModel,
                onClose = {},
                navigateToContentForm = {},
            )
        }

        // Verify the initial state
        // - Verify the error text is displayed
        rule.onNodeWithText("Invalid Content!").assertIsDisplayed()

        // - Verify the scaffold is not displayed
        rule.onAllNodesWithTag("ShowQrCode.Scaffold").assertCountEquals(0)

        // - Verify the 'FullScreenQrCode' is not displayed
        rule.onAllNodesWithTag("ShowQrCode.FullScreenQrCode").assertCountEquals(0)
    }

    @Test
    fun page_portraitContent_test() = runTest {
        rule.requestPortraitOrientation()

        MockableMavericks.initialize(app)

        val onEccChanged = mockk<(ErrorCorrectionCodeLevel) -> Unit>()
        justRun { onEccChanged(any()) }

        val state = ShowQrCodeState(
            currentContent = Fake.Content.realisticText,
            qrCode = Fake.Data.QR_CODE_REAL,
        )

        val viewModel = object : ShowQrCodeVM(state) {
            override val effect = flow<ShowQrCodeEvent> {}
            override fun removeContent() {}
            override fun editContent() {}
            override fun onEccChanged(ecc: ErrorCorrectionCodeLevel) {
                onEccChanged(ecc)
                setState { copy(ecc = ecc) }
            }
        }

        rule.setContent {
            QRCodeMakerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ShowQrCode.Page(
                        viewModel = viewModel,
                        onClose = {},
                        navigateToContentForm = {},
                    )
                }
            }
        }

        // Verify the initial state
        // - Verify the scaffold is displayed
        rule.onNodeWithTag("ShowQrCode.Scaffold").assertIsDisplayed()

        Screenshot.ShowQrCode_Portrait.take()

        // - Verify the error text is not displayed
        rule.onAllNodesWithText("Invalid Content!").assertCountEquals(0)

        // - Verify the 'PageAppBar' is displayed
        rule.onNodeWithTag("ShowQrCode.PageAppBar").assertIsDisplayed()

        // - Verify the 'Toolbox' is displayed
        rule.onNodeWithTag("ShowQrCode.Toolbox").assertIsDisplayed()

        // - Verify the 'FullScreenQrCode' is not displayed
        rule.onAllNodesWithTag("ShowQrCode.FullScreenQrCode").assertCountEquals(0)

        // - Verify the 'PageContent' is in portrait mode
        rule.onNodeWithTag("ShowQrCode.PortraitContent").assertIsDisplayed()

        // -- Verify the QR-Code is displayed
        rule.onNodeWithContentDescription("QR-Code").assertIsDisplayed()

        // -- Verify the 'ErrorCorrection.Module'
        rule.onNodeWithTag("ErrorCorrection.Module").assertIsDisplayed()

            // --- Verify the 'Medium' ECC level is selected
            .onChildren().apply {
                assertCountEquals(3)
                this[0].assertTextEquals("Low").assertIsNotSelected()
                this[1].assertTextEquals("Medium").assertIsSelected()
                this[2].assertTextEquals("High").assertIsNotSelected()
            }

        // -- Verify the QR-Code text
        rule.onNodeWithTag("QR-Code.Text")
            .assertIsDisplayed()
            .assertTextContains("Text:\naaa\nbbb\nccc")

        // Verify actions
        // - Verify ECC action
        // -- Select 'High' ECC level
        rule.onNodeWithText("High")
            .assert(hasParent(hasTestTag("ErrorCorrection.Module")))
            .performClick()

        // -- Verify the ECC level is updated
        // --- Verify the viewModel::onEccChanged is called
        verify { onEccChanged(ErrorCorrectionCodeLevel.High) }

        // --- Verify the viewModel::state is updated
        assertThat(viewModel.awaitState().ecc).isEqualTo(ErrorCorrectionCodeLevel.High)

        // --- Verify the ECC UI module is updated
        rule.onNodeWithTag("ErrorCorrection.Module")
            .onChildren().apply {
                this[0].assertTextEquals("Low").assertIsNotSelected()
                this[1].assertTextEquals("Medium").assertIsNotSelected()
                this[2].assertTextEquals("High").assertIsSelected()
            }

        // - Verify fullscreen action
        // -- Verify the fullscreen QR-Code is not displayed
        rule.onAllNodesWithTag("ShowQrCode.FullScreenQrCode").assertCountEquals(0)

        // -- Click on the QR-Code
        rule.onNodeWithContentDescription("QR-Code").performClick()

        // -- Verify the fullscreen QR-Code is displayed
        rule.onNodeWithTag("ShowQrCode.FullScreenQrCode").assertIsDisplayed()

        Screenshot.ShowQrCode_Portrait_Fullscreen.take()

        // -- Click on the QR-Code
        rule.onAllNodesWithContentDescription("QR-Code")
            .filterToOne(hasTestTag("ShowQrCode.FullScreenQrCode"))
            .performClick()

        // -- Verify the fullscreen QR-Code is not displayed
        rule.onAllNodesWithTag("ShowQrCode.FullScreenQrCode")
            .assertCountEquals(0)

        confirmVerified()
    }

    @Test
    fun page_landscape_content_test() = runTest {
        rule.requestLandscapeOrientation()

        MockableMavericks.initialize(app)

        val onEccChanged = mockk<(ErrorCorrectionCodeLevel) -> Unit>()
        justRun { onEccChanged(any()) }

        val state = ShowQrCodeState(
            currentContent = Fake.Content.realisticText,
            qrCode = Fake.Data.QR_CODE_REAL,
        )

        val viewModel = object : ShowQrCodeVM(state) {
            override val effect = flow<ShowQrCodeEvent> {}
            override fun removeContent() {}
            override fun editContent() {}
            override fun onEccChanged(ecc: ErrorCorrectionCodeLevel) {
                onEccChanged(ecc)
                setState { copy(ecc = ecc) }
            }
        }

        rule.setContent {
            QRCodeMakerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ShowQrCode.Page(
                        viewModel = viewModel,
                        onClose = {},
                        navigateToContentForm = {},
                    )
                }
            }
        }

        // Verify the initial state
        // - Verify the scaffold is displayed
        rule.onNodeWithTag("ShowQrCode.Scaffold").assertIsDisplayed()

        Screenshot.ShowQrCode_Landscape.take()

        // - Verify the error text is not displayed
        rule.onAllNodesWithText("Invalid Content!").assertCountEquals(0)

        // - Verify the 'PageAppBar' is displayed
        rule.onNodeWithTag("ShowQrCode.PageAppBar").assertIsDisplayed()

        // - Verify the 'Toolbox' is displayed
        rule.onNodeWithTag("ShowQrCode.Toolbox").assertIsDisplayed()

        // - Verify the 'FullScreenQrCode' is not displayed
        rule.onAllNodesWithTag("ShowQrCode.FullScreenQrCode").assertCountEquals(0)

        // - Verify the 'PageContent' is in landscape mode
        rule.onNodeWithTag("ShowQrCode.LandscapeContent").assertIsDisplayed()

        // -- Verify the QR-Code is displayed
        rule.onNodeWithContentDescription("QR-Code").assertIsDisplayed()

        // -- Verify the 'ErrorCorrection.Module'
        rule.onNodeWithTag("ErrorCorrection.Module").assertIsDisplayed()

            // --- Verify the 'Medium' ECC level is selected
            .onChildren().apply {
                assertCountEquals(3)
                this[0].assertTextEquals("Low").assertIsNotSelected()
                this[1].assertTextEquals("Medium").assertIsSelected()
                this[2].assertTextEquals("High").assertIsNotSelected()
            }

        // -- Verify the QR-Code text
        rule.onNodeWithTag("QR-Code.Text")
            .assertIsDisplayed()
            .assertTextContains("Text:\naaa\nbbb\nccc")

        // Verify actions
        // - Verify ECC action
        // -- Select 'High' ECC level
        rule.onNodeWithText("High")
            .assert(hasParent(hasTestTag("ErrorCorrection.Module")))
            .performClick()

        // -- Verify the ECC level is updated
        // --- Verify the viewModel::onEccChanged is called
        verify { onEccChanged(ErrorCorrectionCodeLevel.High) }

        // --- Verify the viewModel::state is updated
        assertThat(viewModel.awaitState().ecc).isEqualTo(ErrorCorrectionCodeLevel.High)

        // --- Verify the ECC UI module is updated
        rule.onNodeWithTag("ErrorCorrection.Module")
            .onChildren().apply {
                this[0].assertTextEquals("Low").assertIsNotSelected()
                this[1].assertTextEquals("Medium").assertIsNotSelected()
                this[2].assertTextEquals("High").assertIsSelected()
            }

        // - Verify fullscreen action
        // -- Verify the fullscreen QR-Code is not displayed
        rule.onAllNodesWithTag("ShowQrCode.FullScreenQrCode").assertCountEquals(0)

        // -- Click on the QR-Code
        rule.onNodeWithContentDescription("QR-Code").performClick()

        // -- Verify the fullscreen QR-Code is displayed
        rule.onNodeWithTag("ShowQrCode.FullScreenQrCode").assertIsDisplayed()

        Screenshot.ShowQrCode_Landscape_Fullscreen.take()

        // -- Click on the QR-Code
        rule.onAllNodesWithContentDescription("QR-Code")
            .filterToOne(hasTestTag("ShowQrCode.FullScreenQrCode"))
            .performClick()

        // -- Verify the fullscreen QR-Code is not displayed
        rule.onAllNodesWithTag("ShowQrCode.FullScreenQrCode").assertCountEquals(0)

        confirmVerified()
    }

    @Test
    fun page_Toolbox_test() {
        rule.requestPortraitOrientation()

        MockableMavericks.initialize(app)

        val editContent = mockk<() -> Unit>()
        justRun { editContent() }

        val state = ShowQrCodeState(
            currentContent = Fake.Content.realisticText,
            qrCode = Fake.Data.QR_CODE_REAL,
        )

        val viewModel = object : ShowQrCodeVM(state) {
            override val effect: Flow<ShowQrCodeEvent> = flow { }
            override fun removeContent() {}
            override fun editContent() = editContent()
            override fun onEccChanged(ecc: ErrorCorrectionCodeLevel) {}
        }

        rule.setContent {
            QRCodeMakerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ShowQrCode.Page(
                        viewModel = viewModel,
                        onClose = {},
                        navigateToContentForm = {},
                    )
                }
            }
        }

        // Verify initial state
        rule.onNodeWithTag("ShowQrCode.Toolbox").assertIsDisplayed()

        // Expand the toolbox
        rule.onNodeWithContentDescription("Expand the Toolbox")
            .assertIsDisplayed()
            .performClick()

        // Verify the toolbox is expanded
        rule.onNodeWithContentDescription("Collapse the Toolbox").assertIsDisplayed()
        rule.onNodeWithContentDescription("Remove Content").assertIsDisplayed()
        rule.onNodeWithContentDescription("Edit Content").assertIsDisplayed()

        Screenshot.ShowQrCode_Toolbox.take()

        // Verify actions
        // - Verify edit action
        // -- Click on the edit button
        rule.onNodeWithContentDescription("Edit Content").performClick()

        // -- Verify the viewModel::editContent is called
        verify { editContent() }

        // - Verify the toolbox is collapsed
        rule.onNodeWithContentDescription("Expand the Toolbox").assertIsDisplayed()

            // - Expand the toolbox
            .performClick()

        // - Verify the remove action
        // -- Click on the remove button
        rule.onNodeWithContentDescription("Remove Content").performClick()

        Screenshot.ShowQrCode_Toolbox_RemoveContent.take()

        // -- Verify the remove content bottom is displayed
        rule.onNodeWithTag("Confirmation.Module").assertIsDisplayed()
        rule.onNodeWithText("Remove Content").assertIsDisplayed()
        rule.onNodeWithText("Are you sure you want to remove \"Title\"?").assertIsDisplayed()

        Screenshot.ShowQrCode_Toolbox_RemoveContent.take()

        confirmVerified()
    }

    @Test
    fun handleEffects_test() = runTest {
        rule.requestPortraitOrientation()

        MockableMavericks.initialize(app)

        val onClose = mockk<() -> Unit>()
        val navigateToContentForm = mockk<() -> Unit>()
        justRun { onClose() }
        justRun { navigateToContentForm() }

        val viewModel = object : ShowQrCodeVM(ShowQrCodeState()) {
            val effectChannel = Channel<ShowQrCodeEvent>()
            override val effect = effectChannel.receiveAsFlow()
            override fun removeContent() {}
            override fun editContent() {}
            override fun onEccChanged(ecc: ErrorCorrectionCodeLevel) {}
        }

        rule.setContent {
            ShowQrCode.Page(
                viewModel = viewModel,
                onClose = onClose,
                navigateToContentForm = navigateToContentForm,
            )
        }

        // Verify the 'ClosePage' effect
        // - Trigger the effect
        viewModel.effectChannel.send(ShowQrCodeEvent.ClosePage)

        // - Verify the 'onClose' is called
        verify { onClose() }

        // Verify the 'NavigateToContentForm' effect
        // - Trigger the effect
        viewModel.effectChannel.send(ShowQrCodeEvent.NavigateToContentForm)

        // - Verify the 'navigateToContentForm' is called
        verify { navigateToContentForm() }

        confirmVerified()
    }

    @Test
    fun fullScreenQrCode_test() {
        rule.requestPortraitOrientation()

        val isFullscreen = mutableStateOf(false)

        rule.setContent {
            ShowQrCode.FullScreenQrCode(
                qrCode = Fake.Data.QR_CODE_2x2,
                clickLabel = Fake.Data.LABEL,
                fullScreenAction = actionOf(isFullscreen),
            )
        }

        // Verify initial state
        rule.onAllNodesWithTag("ShowQrCode.FullScreenQrCode").assertCountEquals(0)
        rule.onAllNodesWithContentDescription("QR-Code").assertCountEquals(0)

        // Enter fullscreen
        isFullscreen.value = true

        // Verify entering fullscreen
        rule.onNodeWithTag("ShowQrCode.FullScreenQrCode").assertIsDisplayed()

        // Verify the fullscreen content
        rule.onNodeWithContentDescription("QR-Code").assertIsDisplayed()

            // Exit fullscreen
            .performClick()

        // Verify the fullscreen content is gone
        rule.onAllNodesWithTag("ShowQrCode.FullScreenQrCode").assertCountEquals(0)
        rule.onAllNodesWithContentDescription("QR-Code").assertCountEquals(0)
    }

    @Test
    fun toolbox_test() {
        rule.requestPortraitOrientation()

        val onRemove = mockk<() -> Unit>()
        val onEdit = mockk<() -> Unit>()
        justRun { onRemove() }
        justRun { onEdit() }

        rule.setContent {
            QRCodeMakerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ShowQrCode.Toolbox(
                        onRemove = onRemove,
                        onEdit = onEdit,
                    )
                }
            }
        }

        // Verify the initial state
        // - Verify the toolbox is displayed
        rule.onNodeWithTag("ShowQrCode.Toolbox").assertIsDisplayed()

        // - Verify the 'ToolboxButton'
        rule.onNodeWithContentDescription("Expand the Toolbox").assertIsDisplayed()

            // Expand the toolbox
            .performClick()

        // Verify the toolbox is expanded
        // - Verify the 'ToolboxButton'
        rule.onNodeWithContentDescription("Collapse the Toolbox").assertIsDisplayed()

        // - Verify the 'RemoveContentButton'
        // -- Verify it's displayed
        rule.onNodeWithContentDescription("Remove Content").assertIsDisplayed()

            // -- Verify it's onClick
            .performClick()

        // --- Verify the 'onRemove' is called
        verify { onRemove() }

        // --- Verify the toolbox is collapsed
        rule.onNodeWithContentDescription("Expand the Toolbox").assertIsDisplayed()
        rule.onAllNodesWithContentDescription("Collapse the Toolbox").assertCountEquals(0)
        rule.onAllNodesWithContentDescription("Remove Content").assertCountEquals(0)

        // Expand the toolbox
        rule.onNodeWithContentDescription("Expand the Toolbox").performClick()

        // Verify the toolbox is expanded
        // - Verify the 'EditContentButton'
        // -- Verify it's displayed
        rule.onNodeWithContentDescription("Edit Content").assertIsDisplayed()

            // -- Verify it's onClick
            .performClick()
        // --- Verify 'onEdit' is called
        verify { onEdit() }

        // --- Verify the toolbox is collapsed
        rule.onNodeWithContentDescription("Expand the Toolbox").assertIsDisplayed()
        rule.onAllNodesWithContentDescription("Collapse the Toolbox").assertCountEquals(0)
        rule.onAllNodesWithContentDescription("Remove Content").assertCountEquals(0)

        confirmVerified()
    }

    @Test
    fun editContentButton_test() {
        rule.requestPortraitOrientation()

        val onClick = mockk<() -> Unit>()
        justRun { onClick() }

        rule.setContent {
            ShowQrCode.EditContentButton(onClick)
        }

        // Verify the button is displayed
        rule.onNodeWithContentDescription("Edit Content")
            .assertIsDisplayed()
            .assertHasRole(Role.Button)

            // Click
            .performClick()

        // Verify the 'onClick' is called
        verify { onClick() }

        confirmVerified()
    }

    @Test
    fun removeContentButton_test() {
        rule.requestPortraitOrientation()

        val onClick = mockk<() -> Unit>()
        justRun { onClick() }

        rule.setContent {
            ShowQrCode.RemoveContentButton(onClick)
        }

        // Verify the button is displayed
        rule.onNodeWithContentDescription("Remove Content")
            .assertIsDisplayed()
            .assertHasRole(Role.Button)

            // Click
            .performClick()

        // Verify the onClick is called
        verify { onClick() }

        confirmVerified()
    }

    @Test
    fun toolBoxButton_test() {
        rule.requestPortraitOrientation()

        val expand = mutableStateOf(false)

        rule.setContent {
            ShowQrCode.ToolBoxButton(actionOf(expand))
        }

        // Verify the initial state
        rule.onNodeWithContentDescription("Expand the Toolbox").assertIsDisplayed()

            // Expand the toolbox
            .performClick()

        // - Verify the toolbox is expanded
        assertThat(expand.value).isTrue
        rule.onNodeWithContentDescription("Collapse the Toolbox").assertIsDisplayed()

            // - Collapse the toolbox
            .performClick()

        // - Verify the toolbox is collapsed
        assertThat(expand.value).isFalse
        rule.onNodeWithContentDescription("Expand the Toolbox").assertIsDisplayed()
    }

    @Test
    fun removeConfirmationBottomSheet_test() {
        rule.requestPortraitOrientation()

        val show = mutableStateOf(false)
        val removeContent = mockk<() -> Unit>()
        justRun { removeContent() }

        rule.setContent {
            ShowQrCode.RemoveConfirmationBottomSheet(
                showAction = actionOf(show),
                title = Fake.Data.TITLE,
                removeContent = removeContent,
            )
        }

        // Verify initial state
        // - Verify the bottom sheet is not displayed
        rule.onAllNodesWithTag("Confirmation.Module").assertCountEquals(0)

        // Open the bottom sheet
        show.value = true

        // - Verify the bottom sheet
        // -- Verify it's displayed
        rule.onNodeWithTag("Confirmation.Module").assertIsDisplayed()

        // -- Verify the title
        rule.onNodeWithTag("Confirmation.Module.Title")
            .assertIsDisplayed()
            .assertTextEquals("Remove Content")

        // -- Verify
        rule.onNodeWithTag("Confirmation.Module.Body")
            .assertIsDisplayed()
            .assertTextEquals("Are you sure you want to remove \"Title\"?")

        // -- Verify the remove button
        rule.onNodeWithText("Remove")
            .assertIsDisplayed()
            .assertHasRole(Role.Button)

            // Verify the remove action
            .performClick()
        verify { removeContent() }

        confirmVerified()
    }

    @Test
    fun pageContent_test() {
        rule.requestPortraitOrientation()

        var isPortrait by mutableStateOf(true)
        val ecc = mutableStateOf(ErrorCorrectionCodeLevel.Low)
        val isFullscreen = mutableStateOf(false)

        rule.setContent {
            ShowQrCode.PageContent(
                isPortrait = isPortrait,
                qrCode = Fake.Data.QR_CODE_2x2,
                text = Fake.Data.TEXT,
                eccAction = actionOf(ecc),
                fullScreenAction = actionOf(isFullscreen),
            )
        }

        // Verify the initial state
        rule.onNodeWithTag("ShowQrCode.PortraitContent").assertIsDisplayed()

        // Verify the actions
        // - Verify the Ecc Action
        // -- Verify the 'Low' level is selected
        rule.onNodeWithTag("ErrorCorrection.Module")
            .assertIsDisplayed()
            .onChildren().apply {
                assertCountEquals(3)
                this[0].assertTextEquals("Low").assertIsSelected()
                this[1].assertTextEquals("Medium").assertIsNotSelected()
                this[2].assertTextEquals("High").assertIsNotSelected()
            }
        // -- Select the 'Medium' level
        rule.onNodeWithText("Medium")
            .assert(hasParent(hasTestTag("ErrorCorrection.Module")))
            .performClick()
        // -- Verify the ECC is updated
        assertThat(ecc.value).isEqualTo(ErrorCorrectionCodeLevel.Medium)
        // -- Select the 'High' level
        rule.onNodeWithText("High")
            .assert(hasParent(hasTestTag("ErrorCorrection.Module")))
            .performClick()
        // -- Verify the ECC is updated
        assertThat(ecc.value).isEqualTo(ErrorCorrectionCodeLevel.High)

        // - Verify the fullscreen action
        // -- Click on the QR-Code
        rule.onNodeWithContentDescription("QR-Code")
            .assertIsDisplayed()
            .performClick()
        // -- Verify the fullscreen enter action is requested
        assertThat(isFullscreen.value).isTrue

        // Change the screen orientation
        isPortrait = false

        // - Verify the 'LandscapeContent' is displayed
        rule.onNodeWithTag("ShowQrCode.LandscapeContent").assertIsDisplayed()
    }

    @Test
    fun portraitContent_test() {
        rule.requestPortraitOrientation()

        val ecc = mutableStateOf(ErrorCorrectionCodeLevel.Low)
        val isFullscreen = mutableStateOf(false)

        rule.setContent {
            ShowQrCode.PortraitContent(
                qrCode = Fake.Data.QR_CODE_2x2,
                qrCodeText = Fake.Data.TEXT,
                eccAction = actionOf(ecc),
                fullScreenAction = actionOf(isFullscreen),
            )
        }

        // Verify initial state
        // - Verify the content holder is displayed
        rule.onNodeWithTag("ShowQrCode.PortraitContent").assertIsDisplayed()

        // - Verify the QR-Code is displayed
        rule.onNodeWithContentDescription("QR-Code").assertIsDisplayed()

        // - Verify the ECC data is displayed
        // -- Verify the 'ErrorCorrectionLabel' is displayed
        rule.onNodeWithText("Error Correction Code Level").assertIsDisplayed()

        // -- Verify the ECC option is displayed
        rule.onNodeWithTag("ErrorCorrection.Module")
            .assertIsDisplayed()
            .onChildren().apply {
                assertCountEquals(3)
                this[0].assertTextEquals("Low").assertIsSelected()
                this[1].assertTextEquals("Medium").assertIsNotSelected()
                this[2].assertTextEquals("High").assertIsNotSelected()
            }

        // - Verify the QR-Code text is displayed
        rule.onNodeWithText(Fake.Data.TEXT).assertIsDisplayed()

        // Verify actions
        // - Verify the QR-Code action
        // -- Click on the QR-Code
        rule.onNodeWithContentDescription("QR-Code").performClick()

        // -- Verify the fullscreen enter action is requested
        assertThat(isFullscreen.value).isTrue

        // - Verify the ECC action
        // -- Click on the 'Medium' level
        rule.onNodeWithText("Medium")
            .assert(hasParent(hasTestTag("ErrorCorrection.Module")))
            .performClick()

        // -- Verify the ECC is updated
        assertThat(ecc.value).isEqualTo(ErrorCorrectionCodeLevel.Medium)

        // -- Click on the 'High' level
        rule.onNodeWithText("High")
            .assert(hasParent(hasTestTag("ErrorCorrection.Module")))
            .performClick()

        // -- Verify the ECC is updated
        assertThat(ecc.value).isEqualTo(ErrorCorrectionCodeLevel.High)
    }

    @Test
    fun errorCorrectionLabel_test() {
        rule.requestPortraitOrientation()

        rule.setContent {
            ShowQrCode.ErrorCorrectionLabel()
        }

        // Verify text content
        rule.onNodeWithText("Error Correction Code Level").assertIsDisplayed()
    }

    @Test
    fun landscapeContent_test() {
        rule.requestLandscapeOrientation()

        val ecc = mutableStateOf(ErrorCorrectionCodeLevel.High)
        val isFullscreen = mutableStateOf(false)

        rule.setContent {
            ShowQrCode.LandscapeContent(
                qrCode = Fake.Data.QR_CODE_2x2,
                qrCodeText = Fake.Data.TEXT,
                eccAction = actionOf(ecc),
                fullScreenAction = actionOf(isFullscreen)
            )
        }

        // Verify the initial state
        // - Verify the content holder is displayed
        rule.onNodeWithTag("ShowQrCode.LandscapeContent").assertIsDisplayed()

        // - Verify the QR-Code is displayed
        rule.onNodeWithContentDescription("QR-Code").assertIsDisplayed()

        // - Verify the ECC
        // -- Verify the ECC label is displayed
        rule.onNodeWithText("Error Correction Code Level").assertIsDisplayed()

        // -- Verify the 'High' ECC leve is selected
        rule.onNodeWithTag("ErrorCorrection.Module")
            .assertIsDisplayed()
            .onChildren().apply {
                assertCountEquals(3)
                this[0].assertTextEquals("Low").assertIsNotSelected()
                this[1].assertTextEquals("Medium").assertIsNotSelected()
                this[2].assertTextEquals("High").assertIsSelected()
            }

        // -- Verify the QR-Code Text is displayed
        rule.onNodeWithTag("QR-Code.Text")
            .assertIsDisplayed()
            .assertTextEquals(Fake.Data.TEXT)
    }

    @Test
    fun pageAppBar_test() {
        rule.requestPortraitOrientation()

        rule.setContent {
            ShowQrCode.PageAppBar(title = Fake.Data.TITLE)
        }

        // Verify the PageAppBar is displayed
        rule.onNodeWithTag("ShowQrCode.PageAppBar").assertIsDisplayed()

        // Verify the title is displayed
        rule.onNodeWithText(Fake.Data.TITLE).assertIsDisplayed()
    }

    @Test
    fun qrCodeCanvas_test() {
        rule.requestPortraitOrientation()

        rule.setContent {
            ShowQrCode.QrCodeCanvas(qrCode = Fake.Data.QR_CODE_2x2)
        }

        // Verify the initial state
        rule.onNodeWithContentDescription("QR-Code").assertIsDisplayed()
    }
}
