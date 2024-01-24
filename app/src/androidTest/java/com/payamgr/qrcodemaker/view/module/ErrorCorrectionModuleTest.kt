package com.payamgr.qrcodemaker.view.module

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.assertHasRole
import com.payamgr.qrcodemaker.view.util.actionOf
import org.junit.Rule
import org.junit.Test

@MediumTest
class ErrorCorrectionModuleTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun module_item_test() {
        var isSelected by mutableStateOf(false)
        rule.setContent {
            Row {
                ErrorCorrection.apply {
                    Item(
                        label = Fake.Data.LABEL,
                        isSelected = isSelected,
                        onClick = { isSelected = true },
                    )
                }
            }
        }

        // Verify the label is displayed
        rule.onNodeWithText(Fake.Data.LABEL).assertIsDisplayed()
            // Verify the item has 'Checkbox' role
            .assertHasRole(Role.Checkbox)
            // Verify the item is not selected
            .assertIsNotSelected()

        // Click the item
        rule.onNodeWithText(Fake.Data.LABEL).performClick()

        // Verify the item is selected
        rule.onNodeWithText(Fake.Data.LABEL).assertIsSelected()
    }

    @Test
    fun module_test() {
        val ecc = mutableStateOf(ErrorCorrectionCodeLevel.Low)
        val lowLabel = ErrorCorrectionCodeLevel.Low.toString()
        val mediumLabel = ErrorCorrectionCodeLevel.Medium.toString()
        val highLabel = ErrorCorrectionCodeLevel.High.toString()
        rule.setContent {
            ErrorCorrection.Module(
                eccAction = actionOf(ecc)
            )
        }

        // Verify initial state
        // - Verify 'Low' level selection
        rule.onNodeWithText(lowLabel).assertIsSelected()
        rule.onNodeWithText(mediumLabel).assertIsNotSelected()
        rule.onNodeWithText(highLabel).assertIsNotSelected()

        // 'Medium' level test
        // - Select 'Medium' level
        rule.onNodeWithText(mediumLabel).performClick()

        // - Verify 'Medium' level selection
        rule.onNodeWithText(lowLabel).assertIsNotSelected()
        rule.onNodeWithText(mediumLabel).assertIsSelected()
        rule.onNodeWithText(highLabel).assertIsNotSelected()

        // 'High' level test
        // - Select 'High' level
        rule.onNodeWithText(highLabel).performClick()

        // - Verify 'High' level selection
        rule.onNodeWithText(lowLabel).assertIsNotSelected()
        rule.onNodeWithText(mediumLabel).assertIsNotSelected()
        rule.onNodeWithText(highLabel).assertIsSelected()

        // 'Low' level test
        // - Select 'Low' level
        rule.onNodeWithText(lowLabel).performClick()

        // - Verify 'Low' level selection
        rule.onNodeWithText(lowLabel).assertIsSelected()
        rule.onNodeWithText(mediumLabel).assertIsNotSelected()
        rule.onNodeWithText(highLabel).assertIsNotSelected()
    }
}
