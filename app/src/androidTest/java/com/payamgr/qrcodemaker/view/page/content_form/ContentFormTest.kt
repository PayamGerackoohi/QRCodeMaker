package com.payamgr.qrcodemaker.view.page.content_form

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
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.airbnb.mvrx.mocking.MockableMavericks
import com.payamgr.qrcodemaker.R
import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.database.entity.PhoneCallContent
import com.payamgr.qrcodemaker.data.database.entity.TextContent
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.Input
import com.payamgr.qrcodemaker.data.model.InputData
import com.payamgr.qrcodemaker.data.model.InputId
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.model.event.ContentFormEffect
import com.payamgr.qrcodemaker.data.model.state.ContentFormState
import com.payamgr.qrcodemaker.test_util.ActivityTest
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.Screenshot
import com.payamgr.qrcodemaker.test_util.StringId
import com.payamgr.qrcodemaker.test_util.app
import com.payamgr.qrcodemaker.test_util.assertHasRole
import com.payamgr.qrcodemaker.test_util.take
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.*
import org.junit.Rule
import org.junit.Test

@MediumTest
class ContentFormTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun pageContent_test() {
        val add = mockk<(Content) -> Unit>()
        val showSnackbar = mockk<(String) -> Unit>()
        justRun { add(any()) }
        justRun { showSnackbar(any()) }

        rule.setContent {
            ContentForm.PageContent(
                qrCodeType = QrCodeType.Text(),
                title = Fake.Data.TITLE,
                add = add,
                showSnackbar = showSnackbar,
            )
        }

        // Verify initial state
        // - Verify 'Description'
        rule.onNodeWithContentDescription("Description")
            .assertIsDisplayed()
            .assertTextEquals("Simple text data. Could be used for raw text, web-link, ….")

        // - Verify 'Title'
        rule.onNodeWithTag("ContentForm.Title")
            .assertIsDisplayed()
            .assertTextEquals("* Title", "Title")

        // - Verify inputs
        rule.onNodeWithTag("ContentForm.SingleItem")
            .assertIsDisplayed()
            .assertTextEquals("* Text", "")

        // - Verify 'ConfirmButton'
        rule.onNodeWithTag("ContentForm.ConfirmButton")
            .assertIsDisplayed()
            .assertTextEquals("Confirm")
            .assertHasRole(Role.Button)

        // Empty data error test
        // - Confirm the form
        rule.onNodeWithTag("ContentForm.ConfirmButton")
            .performClick()

        // - Verify snackbar is shown
        verify { showSnackbar("Invalid Input") }

        // Data entry test
        // - Insert 'Title'
        rule.onNodeWithTag("ContentForm.Title").apply {
            performTextClearance()
            performTextInput(Fake.Data.TITLE2)
        }

        // - Insert 'Text'
        rule.onNodeWithTag("ContentForm.SingleItem").performTextInput(Fake.Data.TEXT)

        // - Confirm the form
        rule.onNodeWithTag("ContentForm.ConfirmButton").performClick()

        // - Verify the 'add(Content)'
        verify { add(TextContent(Fake.Data.TITLE2, Fake.Data.TEXT)) }

        confirmVerified()
    }

    @Test
    fun pageAppbar_test() {
        rule.setContent { ContentForm.PageAppBar(title = Fake.Data.TITLE) }

        // Verify the title
        rule.onNodeWithText(Fake.Data.TITLE).assertIsDisplayed()
    }

    @Test
    fun description_Test() {
        val textResource = R.string.name
        val text = app.getString(textResource)
        rule.setContent { ContentForm.Description(descriptionId = textResource) }

        // Verify the text is displayed
        rule.onNodeWithContentDescription("Description")
            .assertIsDisplayed()
            .assertTextEquals(text)
    }

    @Test
    fun title_test() {
        val title = app.getString(R.string.title)
        val map = mutableMapOf<InputId, InputData>()
        rule.setContent {
            ContentForm.Title(
                title = Fake.Data.TITLE,
                addInput = { id, data -> map[id] = data },
                showValidation = false,
            )
        }

        // Verify SingleItem
        rule.onNodeWithTag("ContentForm.Title").apply {
            assertIsDisplayed()
            assertTextEquals("* $title", Fake.Data.TITLE)

            // Insert some text
            performTextClearance()
            performTextInput(Fake.Data.TITLE2)
            rule.waitForIdle()
        }

        // Verify the map is updated
        assertThat(map[InputId.Title]!!.value).isEqualTo(Fake.Data.TITLE2)
    }

    @Test
    fun inputItem_single_test() {
        val name = StringId(R.string.name)
        val map = mutableMapOf<InputId, InputData>()
        rule.setContent {
            ContentForm.InputItem(
                input = Input.Single(
                    id = InputId.Text,
                    labelId = name.resource,
                    initialValue = Fake.Data.BODY,
                    keyboardType = KeyboardType.Text,
                    isOptional = true,
                ),
                addInput = { id, data -> map[id] = data },
                showValidation = false,
            )
        }

        // Verify SingleItem
        rule.onNodeWithTag("ContentForm.SingleItem").apply {
            assertIsDisplayed()
            assertTextEquals(name.label, Fake.Data.BODY)

            // Insert some text
            performTextClearance()
            performTextInput(Fake.Data.BODY2)
            rule.waitForIdle()
        }

        // Verify the map is updated
        assertThat(map[InputId.Text]!!.value).isEqualTo(Fake.Data.BODY2)
    }

    @Test
    fun inputItem_group_test() {
        val title = StringId(R.string.title)
        val name = StringId(R.string.name)
        val map = mutableMapOf<InputId, InputData>()

        rule.setContent {
            ContentForm.InputItem(
                input = Input.Group(
                    titleId = title.resource,
                    inputs = listOf(
                        Input.Single(
                            id = InputId.Text,
                            labelId = name.resource,
                            initialValue = "",
                            keyboardType = KeyboardType.Text,
                            isOptional = false,
                        ),
                    )
                ),
                addInput = { id, data -> map[id] = data },
                showValidation = false,
            )
        }

        // Verify initial state
        // - Verify GroupItem
        rule.onNodeWithTag("ContentForm.GroupItem(${title.label})").assertIsDisplayed()

        // - Verify title
        rule.onNodeWithTag("GroupItem.Title").assertTextEquals(title.label)

        // - Verify the 'SingleItem'
        rule.onNodeWithTag("ContentForm.SingleItem")
            .assertIsDisplayed()
            .assertTextEquals("* ${name.label}", "")

            // Insert some text
            .performTextInput(Fake.Data.TEXT)
        rule.waitForIdle()

        // Verify the map is updated
        assertThat(map[InputId.Text]!!.value).isEqualTo(Fake.Data.TEXT)
    }

    @Test
    fun singleItem_test() {
        val label = StringId(R.string.name)
        val inputId = InputId.Text
        val map = mutableMapOf<InputId, InputData>()
        rule.setContent {
            ContentForm.SingleItem(
                input = Input.Single(
                    id = inputId,
                    labelId = label.resource,
                    initialValue = Fake.Data.BODY,
                    keyboardType = KeyboardType.Text,
                ),
                showValidation = false,
                addInput = { id, data -> map[id] = data },
            )
        }

        // Verify initial state
        // - Verify InputModule.TextType
        rule.onNodeWithTag("ContentForm.SingleItem").apply {
            assertIsDisplayed()

            // - Verify initial value
            assertTextEquals("* ${label.label}", Fake.Data.BODY)

            // - Verify the map content
            assertThat(map[inputId]!!.value).isEqualTo(Fake.Data.BODY)

            // Change the text
            performTextClearance()
            performTextInput(Fake.Data.BODY2)
        }

        rule.waitForIdle()

        // Verify the map is updated
        assertThat(map[inputId]!!.value).isEqualTo(Fake.Data.BODY2)
    }

    @Test
    fun groupItem_test() {
        val title = StringId(R.string.name)
        val firstName = StringId(R.string.name_first_name)
        val lastName = StringId(R.string.name_last_name)
        val map = mutableMapOf<InputId, InputData>()
        rule.setContent {
            ContentForm.GroupItem(
                group = Input.Group(
                    titleId = title.resource,
                    inputs = listOf(
                        Input.Single(
                            id = InputId.FirstName,
                            labelId = firstName.resource,
                            initialValue = "",
                            keyboardType = KeyboardType.Text,
                        ),
                        Input.Single(
                            id = InputId.LastName,
                            labelId = lastName.resource,
                            initialValue = "",
                            keyboardType = KeyboardType.Text,
                        ),
                    ),
                ),
                showValidation = true,
                addInput = { id, data -> map[id] = data },
            )
        }

        // Verify the initial state
        // - Verify the group card
        rule.onNodeWithTag("ContentForm.GroupItem(${title.label})").assertIsDisplayed()

        // - Verify the group title
        rule.onNodeWithTag("GroupItem.Title")
            .assertIsDisplayed()
            .assertTextEquals(app.getString(R.string.name))

        // - Verify the firstName
        rule.onNodeWithText("* ${firstName.label}").assertIsDisplayed()

        // - Verify the lastName
        rule.onNodeWithText("* ${lastName.label}").assertIsDisplayed()

        // Insert the data
        // - Insert the firstName
        rule.onNodeWithText("* ${firstName.label}").performTextInput(Fake.Data.FIRST_NAME)

        // - Insert the lastName
        rule.onNodeWithText("* ${lastName.label}").performTextInput(Fake.Data.LAST_NAME)

        rule.waitForIdle()

        // - Verify the firstName
        assertThat(map[InputId.FirstName]!!.value).isEqualTo(Fake.Data.FIRST_NAME)

        // - Verify the lastName
        assertThat(map[InputId.LastName]!!.value).isEqualTo(Fake.Data.LAST_NAME)
    }

    @Test
    fun confirmButton_test() {
        val addContent = mockk<(Content) -> Unit>()
        val showSnackbar = mockk<(String) -> Unit>()
        justRun { addContent(any()) }
        justRun { showSnackbar(any()) }
        var areInputsValid by mutableStateOf(true)

        rule.setContent {
            ContentForm.ConfirmButton(
                qrCodeType = QrCodeType.Text(),
                inputMap = mapOf(
                    InputId.Title to InputData(Fake.Data.TITLE),
                    InputId.Text to InputData(Fake.Data.TEXT),
                ),
                areInputsValid = { areInputsValid },
                onShowValidationChanged = {},
                addContent = addContent,
                showSnackbar = showSnackbar,
            )
        }

        // Verify initial state
        rule.onNodeWithText("Confirm").assertIsDisplayed()

            // Valid inputs scenario
            .performClick()

        // - Verify the content
        verify { addContent(TextContent(Fake.Data.TITLE, Fake.Data.TEXT)) }

        // Invalid inputs scenario
        areInputsValid = false
        rule.onNodeWithText("Confirm").performClick()

        // - Verify the content
        verify { showSnackbar("Invalid Input") }

        confirmVerified()
    }
}

@LargeTest
class ContentFormActivityTest : ActivityTest() {
    @Test
    fun page_insertMode_Text_test() = runTest {
        MockableMavericks.initialize(app)

        val qrCodeTypeError = "No currentQrCodeType!"

        val add = mockk<(Content) -> Unit>()
        val onClose = mockk<() -> Unit>()
        justRun { add(any()) }
        justRun { onClose() }

        val state = ContentFormState(contentTitle = Fake.Data.TITLE)

        val viewModel = object : ContentFormVM(state) {
            var effectChannel = Channel<ContentFormEffect>()
            override val effect: Flow<ContentFormEffect> = effectChannel.receiveAsFlow()
            override fun add(content: Content) = add(content)
            override fun update(content: Content) {}
            fun update(qrCodeType: QrCodeType) = setState { copy(currentQrCodeType = qrCodeType) }
        }

        rule.setContent {
            QRCodeMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContentForm.Page(
                        viewModel = viewModel,
                        onClose = { onClose() },
                        isEditMode = false,
                    )
                }
            }
        }

        // 'No currentQrCodeType' test
        rule.onNodeWithText(qrCodeTypeError).assertIsDisplayed()

        // Verify initial state
        viewModel.update(QrCodeType.Text())

        // - Verify no QR-Code type error
        rule.waitUntil {
            rule.onAllNodesWithText(qrCodeTypeError).fetchSemanticsNodes().isEmpty()
        }

        // - Verify TopAppBar
        rule.onNodeWithTag("ContentForm.PageAppBar")
            .assertIsDisplayed()
            .onChild()
            .assertTextEquals(app.getString(R.string.text))

        Screenshot.ContentForm_InsertMode_Text.take()

        // - Verify PageContent
        rule.onNodeWithTag("ContentForm.PageContent").assertIsDisplayed()

        // -- Verify 'Description'
        rule.onNodeWithContentDescription("Description")
            .assertIsDisplayed()
            .assertTextEquals("Simple text data. Could be used for raw text, web-link, ….")

        // -- Verify 'Title'
        rule.onNodeWithTag("ContentForm.Title")
            .assertIsDisplayed()
            .assertTextEquals("* Title", "")

        // -- Verify inputs
        rule.onNodeWithTag("ContentForm.SingleItem")
            .assertIsDisplayed()
            .assertTextEquals("* Text", "")

        // -- Verify 'ConfirmButton'
        rule.onNodeWithTag("ContentForm.ConfirmButton")
            .assertIsDisplayed()
            .assertTextEquals("Confirm")
            .assertHasRole(Role.Button)

        // Data entry test
        // - Insert inputs
        rule.onNodeWithTag("ContentForm.Title")
            .performTextInput(Fake.Data.TITLE)
        rule.onNodeWithTag("ContentForm.SingleItem")
            .performTextInput(Fake.Data.TEXT)

        // - Confirm the form
        rule.onNodeWithTag("ContentForm.ConfirmButton").performClick()

        // - Verify 'viewModel.add(Content)' is called
        verify { add(TextContent(Fake.Data.TITLE, Fake.Data.TEXT)) }

        // Events Test
        // - Sending ContentFormEvent.ClosePage
        viewModel.effectChannel.send(ContentFormEffect.ClosePage)

        // - Verify 'onClose' is called
        verify { onClose() }

        confirmVerified()
    }

    @Test
    fun page_insertMode_PhoneCall_test() = runTest {
        MockableMavericks.initialize(app)

        val qrCodeTypeError = "No currentQrCodeType!"

        val add = mockk<(Content) -> Unit>()
        val onClose = mockk<() -> Unit>()
        justRun { add(any()) }
        justRun { onClose() }

        val state = ContentFormState(contentTitle = Fake.Data.TITLE)

        val viewModel = object : ContentFormVM(state) {
            var effectChannel = Channel<ContentFormEffect>()
            override val effect: Flow<ContentFormEffect> = effectChannel.receiveAsFlow()
            override fun add(content: Content) = add(content)
            override fun update(content: Content) {}
            fun update(qrCodeType: QrCodeType) = setState { copy(currentQrCodeType = qrCodeType) }
        }

        rule.setContent {
            QRCodeMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContentForm.Page(
                        viewModel = viewModel,
                        onClose = { onClose() },
                        isEditMode = false,
                    )
                }
            }
        }

        // 'No currentQrCodeType' test
        rule.onNodeWithText(qrCodeTypeError).assertIsDisplayed()

        // Verify initial state
        viewModel.update(QrCodeType.PhoneCall())

        // - Verify no QR-Code type error
        rule.waitUntil {
            rule.onAllNodesWithText(qrCodeTypeError).fetchSemanticsNodes().isEmpty()
        }

        // - Verify TopAppBar
        rule.onNodeWithTag("ContentForm.PageAppBar")
            .assertIsDisplayed()
            .onChild()
            .assertTextEquals(app.getString(R.string.phone_call))

        Screenshot.ContentForm_InsertMode_PhoneCall.take()

        // - Verify PageContent
        rule.onNodeWithTag("ContentForm.PageContent").assertIsDisplayed()

        // -- Verify 'Description'
        rule.onNodeWithContentDescription("Description")
            .assertIsDisplayed()
            .assertTextEquals("The QrCode reader can suggest calling the number.")

        // -- Verify 'Title'
        rule.onNodeWithTag("ContentForm.Title")
            .assertIsDisplayed()
            .assertTextEquals("* Title", "")

        // -- Verify inputs
        rule.onNodeWithTag("ContentForm.SingleItem")
            .assertIsDisplayed()
            .assertTextEquals("* Phone", "")

        // -- Verify 'ConfirmButton'
        rule.onNodeWithTag("ContentForm.ConfirmButton")
            .assertIsDisplayed()
            .assertTextEquals("Confirm")
            .assertHasRole(Role.Button)

        // Data entry test
        // - Insert inputs
        rule.onNodeWithTag("ContentForm.Title").performTextInput(Fake.Data.TITLE)
        rule.onNodeWithTag("ContentForm.SingleItem").performTextInput(Fake.Data.PHONE)

        // - Confirm the form
        rule.onNodeWithTag("ContentForm.ConfirmButton").performClick()

        // - Verify 'viewModel.add(Content)' is called
        verify { add(PhoneCallContent(Fake.Data.TITLE, Fake.Data.PHONE)) }

        // Events Test
        // - Sending ContentFormEvent.ClosePage
        viewModel.effectChannel.send(ContentFormEffect.ClosePage)

        // - Verify 'onClose' is called
        verify { onClose() }

        confirmVerified()
    }

    @Test
    fun page_insertMode_MeCard_test() = runTest {
        MockableMavericks.initialize(app)

        val qrCodeTypeError = "No currentQrCodeType!"

        val add = mockk<(Content) -> Unit>()
        val onClose = mockk<() -> Unit>()
        justRun { add(any()) }
        justRun { onClose() }

        val state = ContentFormState(contentTitle = Fake.Data.TITLE)

        val viewModel = object : ContentFormVM(state) {
            var effectChannel = Channel<ContentFormEffect>()
            override val effect: Flow<ContentFormEffect> = effectChannel.receiveAsFlow()
            override fun add(content: Content) = add(content)
            override fun update(content: Content) {}
            fun update(qrCodeType: QrCodeType) = setState { copy(currentQrCodeType = qrCodeType) }
        }

        rule.setContent {
            QRCodeMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContentForm.Page(
                        viewModel = viewModel,
                        onClose = { onClose() },
                        isEditMode = false,
                    )
                }
            }
        }

        // 'No currentQrCodeType' test
        rule.onNodeWithText(qrCodeTypeError).assertIsDisplayed()

        // Verify initial state
        viewModel.update(QrCodeType.MeCard())

        // - Verify no QR-Code type error
        rule.waitUntil {
            rule.onAllNodesWithText(qrCodeTypeError).fetchSemanticsNodes().isEmpty()
        }

        // - Verify TopAppBar
        rule.onNodeWithTag("ContentForm.PageAppBar")
            .assertIsDisplayed()
            .onChild()
            .assertTextEquals(app.getString(R.string.me_card))

        // - Verify PageContent
        rule.onNodeWithTag("ContentForm.PageContent").assertIsDisplayed()

        // -- Verify 'Description'
        rule.onNodeWithContentDescription("Description")
            .assertIsDisplayed()
            .assertTextEquals("The QrCode reader can suggest adding the Me-Card to the contacts.")

        // -- Verify 'Title'
        rule.onNodeWithTag("ContentForm.Title")
            .assertIsDisplayed()
            .assertTextEquals("* Title", "")

        // -- Verify inputs
        // --- Verify the 'Name' group input
        rule.onNodeWithTag("ContentForm.GroupItem(Name)")
            .assertIsDisplayed()
            .onChildren().apply {
                assertCountEquals(3)
                this[0].assert(hasTestTag("GroupItem.Title"))
                    .assertIsDisplayed()
                    .assertTextEquals("Name")

                this[1].assert(hasTestTag("ContentForm.SingleItem"))
                    .assertIsDisplayed()
                    .assertTextEquals("* First Name", "")

                this[2].assert(hasTestTag("ContentForm.SingleItem"))
                    .assertIsDisplayed()
                    .assertTextEquals("* Last Name", "")
            }

        // --- Verify the 'Phone' input
        rule.onAllNodesWithTag("ContentForm.SingleItem")
            .filterToOne(hasText("* Phone"))
            .assertIsDisplayed()

        // -- Verify 'ConfirmButton'
        rule.onNodeWithTag("ContentForm.ConfirmButton")
            .assertIsDisplayed()
            .assertTextEquals("Confirm")
            .assertHasRole(Role.Button)

        Screenshot.ContentForm_InsertMode_MeCard.take()

        // Data entry test
        // - Insert inputs
        rule.onNodeWithTag("ContentForm.Title").performTextInput(Fake.Data.TITLE)
        rule.onAllNodesWithTag("ContentForm.SingleItem").apply {
            this[0].performTextInput(Fake.Data.FIRST_NAME)
            this[1].performTextInput(Fake.Data.LAST_NAME)
            this[2].performTextInput(Fake.Data.PHONE)
        }

        // - Confirm the form
        rule.onNodeWithTag("ContentForm.ConfirmButton").performClick()

        // - Verify 'viewModel.add(Content)' is called
        verify { add(Fake.Data.run { MeCardContent(TITLE, FIRST_NAME, LAST_NAME, PHONE) }) }

        // Events Test
        // - Sending ContentFormEvent.ClosePage
        viewModel.effectChannel.send(ContentFormEffect.ClosePage)

        // - Verify 'onClose' is called
        verify { onClose() }

        confirmVerified()
    }

    @Test
    fun page_edit_mode_test() = runTest {
        MockableMavericks.initialize(app)

        val qrCodeTypeError = "No currentQrCodeType!"

        val update = mockk<(Content) -> Unit>()
        val onClose = mockk<() -> Unit>()
        justRun { update(any()) }
        justRun { onClose() }

        val state = ContentFormState(
            currentContent = Fake.Content.text,
            contentTitle = Fake.Data.TITLE,
        )

        val viewModel = object : ContentFormVM(state) {
            var eventChannel = Channel<ContentFormEffect>()
            override val effect: Flow<ContentFormEffect> = eventChannel.receiveAsFlow()
            override fun add(content: Content) {}
            override fun update(content: Content) = update(content)
            fun update(qrCodeType: QrCodeType) = setState { copy(currentQrCodeType = qrCodeType) }
        }

        rule.setContent {
            QRCodeMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContentForm.Page(
                        viewModel = viewModel,
                        onClose = { onClose() },
                        isEditMode = true,
                    )
                }
            }
        }

        // 'No currentQrCodeType' test
        rule.onNodeWithText(qrCodeTypeError).assertIsDisplayed()

        // Verify initial state
        viewModel.update(QrCodeType.Text(Fake.Data.TEXT))

        // - Verify no QR-Code type error
        rule.waitUntil {
            rule.onAllNodesWithText(qrCodeTypeError).fetchSemanticsNodes().isEmpty()
        }

        // - Verify TopAppBar
        rule.onNodeWithTag("ContentForm.PageAppBar")
            .assertIsDisplayed()
            .onChild()
            .assertTextEquals(app.getString(R.string.text))

        Screenshot.ContentForm_EditMode.take()

        // - Verify PageContent
        rule.onNodeWithTag("ContentForm.PageContent").assertIsDisplayed()

        // -- Verify 'Description'
        rule.onNodeWithContentDescription("Description")
            .assertIsDisplayed()
            .assertTextEquals("Simple text data. Could be used for raw text, web-link, ….")

        // -- Verify 'Title'
        rule.onNodeWithTag("ContentForm.Title")
            .assertIsDisplayed()
            .assertTextEquals("* Title", "Title")

        // -- Verify inputs
        rule.onNodeWithTag("ContentForm.SingleItem")
            .assertIsDisplayed()
            .assertTextEquals("* Text", "Text-txeT")

        // -- Verify 'ConfirmButton'
        rule.onNodeWithTag("ContentForm.ConfirmButton")
            .assertIsDisplayed()
            .assertTextEquals("Confirm")
            .assertHasRole(Role.Button)

        // Data entry test
        // - Insert inputs
        rule.onNodeWithTag("ContentForm.Title").apply {
            performTextClearance()
            performTextInput(Fake.Data.TITLE2)
        }
        rule.onNodeWithTag("ContentForm.SingleItem").apply {
            performTextClearance()
            performTextInput(Fake.Data.TEXT2)
        }

        // - Confirm the form
        rule.onNodeWithTag("ContentForm.ConfirmButton").performClick()

        // - Verify 'viewModel.update(Content)' is called
        verify { update(TextContent(Fake.Data.TITLE2, Fake.Data.TEXT2)) }

        // Events Test
        // - Sending ContentFormEvent.ClosePage
        viewModel.eventChannel.send(ContentFormEffect.ClosePage)

        // - Verify 'onClose' is called
        verify { onClose() }

        confirmVerified()
    }
}
