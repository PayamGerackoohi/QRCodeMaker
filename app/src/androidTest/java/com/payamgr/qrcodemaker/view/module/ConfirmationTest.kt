package com.payamgr.qrcodemaker.view.module

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.filters.MediumTest
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.assertHasRole
import com.payamgr.qrcodemaker.view.util.actionOf
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.*
import org.junit.Rule
import org.junit.Test

@MediumTest
class ConfirmationTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun moduleButton_test() {
        val onClick = mockk<() -> Unit>()
        justRun { onClick() }

        rule.setContent {
            Row {
                Confirmation.apply {
                    ModuleButton(
                        label = Fake.Data.LABEL,
                        onClick = onClick,
                    )
                }
            }
        }

        // Verify the label is shown
        rule.onNodeWithText(Fake.Data.LABEL).assertIsDisplayed()
            // Verify the button role
            .assertHasRole(Role.Button)
            // Click on the module
            .performClick()

        // Verify the 'onClick' is called
        verify { onClick() }

        confirmVerified()
    }

    @Test
    fun module_empty_ok_dialog_test() {
        val show = mutableStateOf(false)
        val okCalled = mockk<() -> Unit>()
        justRun { okCalled() }

        rule.setContent {
            Confirmation.Module(
                showAction = actionOf(show),
                actions = arrayOf(ActionType(Fake.Data.OK, okCalled)),
            )
        }

        // Verify the dialog is not shown
        rule.onNodeWithTag("Confirmation.Module").assertDoesNotExist()

        // Request to show the dialog
        show.value = true

        // Verify the dialog is shown
        rule.onNodeWithTag("Confirmation.Module").assertIsDisplayed()

        // Verify the title text does not exist
        rule.onAllNodesWithTag("Confirmation.Module.Title").assertCountEquals(0)

        // Verify the body text does not exist
        rule.onAllNodesWithTag("Confirmation.Module.Body").assertCountEquals(0)

        // 'OK' scenario
        // - Verify the 'OK' button is shown
        rule.onNodeWithText(Fake.Data.OK).assertIsDisplayed()
            // - Select 'OK' action
            .performClick()

        // - Verify 'OK' action is called
        verify { okCalled() }

        // - Verify the dialog is hidden
        rule.onNodeWithTag("Confirmation.Module").assertDoesNotExist()
        assertThat(show.value).isFalse

        confirmVerified()
    }

    @Test
    fun module_full_yes_no_data_test() {
        val show = mutableStateOf(false)
        val yesCalled = mockk<() -> Unit>()
        justRun { yesCalled() }
        val noCalled = mockk<() -> Unit>()
        justRun { noCalled() }

        rule.setContent {
            Confirmation.Module(
                title = Fake.Data.TITLE,
                body = Fake.Data.BODY,
                showAction = actionOf(show),
                actions = arrayOf(
                    ActionType("Yes", yesCalled),
                    ActionType("No", noCalled),
                ),
            )
        }
        // Verify the dialog is not shown
        rule.onNodeWithTag("Confirmation.Module").assertDoesNotExist()

        // Request to show the dialog
        show.value = true

        // Verify the dialog is shown
        rule.onNodeWithTag("Confirmation.Module").assertIsDisplayed()

        // Verify the title text is shown
        rule.onNodeWithTag("Confirmation.Module.Title")
            .assertIsDisplayed()
            .assertTextEquals(Fake.Data.TITLE)

        // Verify the body text is shown
        rule.onNodeWithTag("Confirmation.Module.Body")
            .assertIsDisplayed()
            .assertTextEquals(Fake.Data.BODY)

        // Verify the 'Yes' button is shown
        rule.onNodeWithText(Fake.Data.YES).assertIsDisplayed()

        // Verify the 'No' button is shown
        rule.onNodeWithText(Fake.Data.NO).assertIsDisplayed()

        // 'Yes' scenario
        // - Select 'Yes' action
        rule.onNodeWithText(Fake.Data.YES)
            .assertIsDisplayed()
            .performClick()

        // - Verify 'Yes' action is called
        verify { yesCalled() }

        // - Verify the dialog is hidden
        rule.onNodeWithTag("Confirmation.Module").assertDoesNotExist()
        assertThat(show.value).isFalse

        // Request to show the dialog
        show.value = true

        // Verify the dialog is shown
        rule.onNodeWithTag("Confirmation.Module").assertIsDisplayed()

        // 'No' scenario
        // - Select 'No' action
        rule.onNodeWithText(Fake.Data.NO).performClick()

        // - Verify 'No' action is called
        verify { noCalled() }

        // - Verify the dialog is hidden
        rule.onNodeWithTag("Confirmation.Module").assertDoesNotExist()
        assertThat(show.value).isFalse

        confirmVerified()
    }

    @Test
    fun module_dismiss_dialog_test() {
        val show = mutableStateOf(true)
        rule.setContent {
            Confirmation.Module(
                title = Fake.Data.TITLE,
                body = Fake.Data.BODY,
                showAction = actionOf(show),
                actions = arrayOf(ActionType("OK", {})),
            )
        }

        // Verify the dialog is shown
        rule.onNodeWithTag("Confirmation.Module").assertIsDisplayed()

        // Dismiss scenario
        // - Dismiss the dialog
        rule.onNodeWithTag("Confirmation.Module").performTouchInput { swipeDown() }

        // - Verify the dialog is hidden
        rule.onNodeWithTag("Confirmation.Module").assertDoesNotExist()
        assertThat(show.value).isFalse

        confirmVerified()
    }
}
