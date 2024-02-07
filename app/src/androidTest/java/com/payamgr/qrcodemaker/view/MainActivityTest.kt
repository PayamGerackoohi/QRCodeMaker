package com.payamgr.qrcodemaker.view

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.filters.LargeTest
import com.payamgr.qrcodemaker.R
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.app
import org.junit.Rule
import org.junit.Test

@LargeTest
class MainActivityTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun e2e() {
        // For for the splashscreen animation to end
        rule.waitUntil {
            rule.onAllNodesWithTag("Qr-Code Contents").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify 'Home' page
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()

        // Click the 'Add Content' button
        rule.onNodeWithContentDescription("Add Content")

            // Add content
            .performClick()

        // Verify the 'ContentType' page
        rule.onNodeWithTag("QR-Code Types").assertIsDisplayed()
        rule.onAllNodesWithContentDescription("Add Content").assertCountEquals(0)

        // Select 'Text' type
        rule.waitUntil {
            rule.onAllNodesWithText(app.getString(R.string.text)).fetchSemanticsNodes().size == 1
        }
        rule.onNodeWithText(app.getString(R.string.text)).performClick()

        // Verify the 'ContentForm' page
        rule.onNodeWithTag("ContentForm.PageContent").assertIsDisplayed()

        // Input Data
        rule.onNodeWithTag("ContentForm.Title").performTextInput(Fake.Data.TITLE)
        rule.onNodeWithText("* Text")
            .assert(hasTestTag("ContentForm.SingleItem"))
            .performTextInput(Fake.Data.TEXT)

        // Confirm the form
        rule.onNodeWithText("Confirm")
            .assert(hasTestTag("ContentForm.ConfirmButton"))
            .performClick()

        // Verify 'Home' page
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()

        // Verify the content
        rule.onNodeWithText(Fake.Data.TITLE)
            .assertIsDisplayed()

            // Click the content
            .performClick()

        // Verify the 'ShowQrCode' page
        rule.onNodeWithTag("ShowQrCode.Scaffold").assertIsDisplayed()
        rule.onNodeWithTag("ShowQrCode.PortraitContent").assertIsDisplayed()
        rule.onNodeWithContentDescription("QR-Code").assertIsDisplayed()
        rule.onNodeWithTag("ErrorCorrection.Module")
            .assertIsDisplayed()
            .onChildAt(1)
            .assertTextEquals("Medium")
            .assertIsSelected()
        rule.onAllNodesWithText(Fake.Data.TITLE)
            .filterToOne(hasParent(hasTestTag("ShowQrCode.PageAppBar")))
            .assertIsDisplayed()
        rule.onNodeWithTag("QR-Code.Text")
            .assertIsDisplayed()
            .assertTextEquals("Text:\n${Fake.Data.TEXT}")

        // Change the ECC
        // - Click the 'High' ECC level
        rule.onAllNodesWithText("High")
            .filterToOne(hasParent(hasTestTag("ErrorCorrection.Module")))
            .performClick()
        // - Verify the ECC level is updated
        rule.onNodeWithTag("ErrorCorrection.Module")
            .assertIsDisplayed()
            .onChildAt(2)
            .assertTextEquals("High")
            .assertIsSelected()

        // Verify the fullscreen QR-Code
        rule.onNodeWithContentDescription("QR-Code").performClick()
        rule.onNodeWithTag("ShowQrCode.FullScreenQrCode").assertIsDisplayed()

            // Close the fullscreen
            .performClick()

        // Open the 'Toolbox'
        rule.onNodeWithContentDescription("Expand the Toolbox").performClick()

        // Edit the content
        rule.onNodeWithContentDescription("Edit Content").performClick()

        // Verify the 'ContentForm' page
        rule.onNodeWithTag("ContentForm.PageContent").assertIsDisplayed()

        // Edit the data
        rule.onNodeWithText("* Title")
            .assertIsDisplayed().apply {
                assertTextEquals("* Title", Fake.Data.TITLE)
                performTextClearance()
                performTextInput(Fake.Data.TITLE2)
            }
        rule.onNodeWithText("* Text")
            .assertIsDisplayed().apply {
                assertTextEquals("* Text", Fake.Data.TEXT)
                performTextClearance()
                performTextInput(Fake.Data.TEXT2)
            }

        // Confirm the form
        rule.onAllNodesWithText("Confirm")
            .filterToOne(hasTestTag("ContentForm.ConfirmButton"))
            .performClick()

        // Verify the 'ShowQrCode' page
        rule.onNodeWithTag("ShowQrCode.Scaffold").assertIsDisplayed()

        // Verify the content is updated
        rule.onAllNodesWithText(Fake.Data.TITLE2)
            .filterToOne(hasParent(hasTestTag("ShowQrCode.PageAppBar")))
            .assertIsDisplayed()
        rule.onNodeWithTag("QR-Code.Text")
            .assertIsDisplayed()
            .assertTextEquals("Text:\n${Fake.Data.TEXT2}")

        // Press back button
        Espresso.pressBack()

        // Verify the 'Home' page
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()

        // Verify the content is updated
        rule.onNodeWithText(Fake.Data.TITLE2).assertIsDisplayed()

            // Open the 'ShowQrCode' page
            .performClick()

        // Verify the 'ShowQrCode' page
        rule.onNodeWithTag("ShowQrCode.Scaffold").assertIsDisplayed()

        // Open the toolbox
        rule.onNodeWithContentDescription("Expand the Toolbox").performClick()

        // Remove the content
        rule.onNodeWithContentDescription("Remove Content").performClick()
        rule.onAllNodesWithText("Remove")
            .filterToOne(hasParent(hasTestTag("Confirmation.Module")))
            .performClick()

        // Verify 'Home' page
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()

        // Verify the content list is empty
        rule.onNodeWithTag("Qr-Code Contents")
            .onChildren()
            .assertCountEquals(0)
    }
}
