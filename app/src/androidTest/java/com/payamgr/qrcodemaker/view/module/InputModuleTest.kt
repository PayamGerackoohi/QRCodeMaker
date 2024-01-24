package com.payamgr.qrcodemaker.view.module

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.test.filters.MediumTest
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.assertDoesNotHaveError
import com.payamgr.qrcodemaker.test_util.assertHasError
import com.payamgr.qrcodemaker.view.util.actionOf
import org.assertj.core.api.Assertions.*
import org.junit.Rule
import org.junit.Test

@MediumTest
class InputModuleTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun mandatory_module_test() {
        val text = mutableStateOf("")
        rule.setContent {
            InputModule.TextType(
                valueAction = actionOf(text),
                label = Fake.Data.LABEL,
                keyboardType = KeyboardType.Text,
                isMandatory = true,
                isError = false,
            )
        }

        // Verify initial state
        // - Verify the label has '*'
        rule.onNodeWithText("* ${Fake.Data.LABEL}").assertIsDisplayed()

        rule.onNodeWithTag("InputModule.TextType.TextField").apply {
            // - Verify the input is empty
            assertTextEquals("* ${Fake.Data.LABEL}", "")
            assertThat(text.value).isEmpty()

            // - Verify there is no error
            assertDoesNotHaveError()

            // Insert some text
            performTextInput(Fake.Data.BODY)

            // Verify the text is applied
            assertTextEquals("* ${Fake.Data.LABEL}", Fake.Data.BODY)
            assertThat(text.value).isEqualTo(Fake.Data.BODY)
        }
    }

    @Test
    fun optional_module_test() {
        val text = mutableStateOf(Fake.Data.BODY)
        rule.setContent {
            InputModule.TextType(
                valueAction = actionOf(text),
                label = Fake.Data.LABEL,
                keyboardType = KeyboardType.Text,
                isMandatory = false,
                isError = false,
            )
        }

        // Verify initial state
        // - Verify the label does not have '*'
        rule.onNodeWithText(Fake.Data.LABEL).assertIsDisplayed()

        // - Verify the input is empty
        rule.onNodeWithTag("InputModule.TextType.TextField").apply {
            assertTextEquals(Fake.Data.LABEL, Fake.Data.BODY)
            assertThat(text.value).isEqualTo(Fake.Data.BODY)

            // - Verify there is no error
            assertDoesNotHaveError()

            // Insert some text
            performTextClearance()
            performTextInput(Fake.Data.BODY2)

            // Verify the text is applied
            assertTextEquals(Fake.Data.LABEL, Fake.Data.BODY2)
            assertThat(text.value).isEqualTo(Fake.Data.BODY2)
        }
    }

    @Test
    fun error_module_test() {
        val text = mutableStateOf(Fake.Data.BODY)
        var hasError by mutableStateOf(true)
        rule.setContent {
            InputModule.TextType(
                valueAction = actionOf(text),
                label = Fake.Data.LABEL,
                keyboardType = KeyboardType.Text,
                isMandatory = false,
                isError = hasError,
            )
        }

        // Verify initial state
        // - Verify the label does not have '*'
        rule.onNodeWithText(Fake.Data.LABEL).assertIsDisplayed()

        // - Verify the input is empty
        rule.onNodeWithTag("InputModule.TextType.TextField").apply {
            assertTextEquals(Fake.Data.LABEL, Fake.Data.BODY)
            assertThat(text.value).isEqualTo(Fake.Data.BODY)

            // - Verify there is an error
            assertHasError()

            // Clear error flag
            hasError = false

            // Verify the error is cleared
            assertDoesNotHaveError()
        }
    }
}
