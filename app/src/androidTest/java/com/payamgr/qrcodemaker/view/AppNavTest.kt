package com.payamgr.qrcodemaker.view

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso
import androidx.test.filters.MediumTest
import com.airbnb.mvrx.mocking.MockableMavericks
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.test_util.C
import com.payamgr.qrcodemaker.test_util.Fake
import com.payamgr.qrcodemaker.test_util.app
import com.payamgr.qrcodemaker.test_util.assertCurrentRoute
import com.payamgr.qrcodemaker.view.page.content_form.ContentForm
import com.payamgr.qrcodemaker.view.page.content_type.ContentType
import com.payamgr.qrcodemaker.view.page.home.Home
import com.payamgr.qrcodemaker.view.page.show_qrcode.ShowQrCode
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

@MediumTest
class AppNavTest {
    @get:Rule
    val rule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Test
    fun verifyStartDestination() {
        setup()

        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()
        navController.assertCurrentRoute(Home.Route)
    }

    @Test
    fun homeToContentType_navigation_test() {
        setup()

        // Verify 'Home' destination
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()
        rule.onAllNodesWithTag("QR-Code Types").assertCountEquals(0)
        navController.assertCurrentRoute(Home.Route)

        // Click on the 'Add Content' button
        rule.onNodeWithContentDescription("Add Content").performClick()

        // Verify the 'ContentType' Page is opened
        rule.onNodeWithTag("QR-Code Types").assertIsDisplayed()
        rule.onAllNodesWithTag("Qr-Code Contents").assertCountEquals(0)
        navController.assertCurrentRoute(ContentType.Route)

        // Verify the back press navigates to 'Home' Page
        Espresso.pressBack()
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()
        rule.onAllNodesWithTag("QR-Code Types").assertCountEquals(0)
        navController.assertCurrentRoute(Home.Route)
    }

    @Test
    fun homeToShowQrCode_navigation_test() {
        setup(
            Fake.ViewModelBuilder(object : Fake.ContentRepository() {
                override val currentContent = MutableStateFlow<C?>(Fake.Content.text)
                override suspend fun loadContents() = MutableStateFlow(listOf<Content>(Fake.Content.text))
            })
        )

        // Verify 'Home' destination
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()
        rule.onAllNodesWithTag("ShowQrCode.Scaffold").assertCountEquals(0)
        navController.assertCurrentRoute(Home.Route)

        // Click on the 'Text' content
        rule.onNodeWithText("Sample Text").performClick()

        // Verify the 'ShowQrCode' destination
        rule.onNodeWithTag("ShowQrCode.Scaffold").assertIsDisplayed()
        rule.onAllNodesWithTag("Qr-Code Contents").assertCountEquals(0)
        navController.assertCurrentRoute(ShowQrCode.Route)

        // Verify the back press navigates to 'Home' page
        Espresso.pressBack()
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()
        rule.onAllNodesWithTag("ShowQrCode.Scaffold").assertCountEquals(0)
        navController.assertCurrentRoute(Home.Route)
    }

    @Test
    fun contentTypeToContentForm_navigation_test() {
        setup(Fake.ViewModelBuilder(object : Fake.ContentRepository() {
            override val currentQrCodeType = MutableStateFlow<QrCodeType?>(QrCodeType.Text())
        }))

        // Open the 'ContentType' page
        rule.onNodeWithContentDescription("Add Content").performClick()

        // Verify the 'ContentType' destination
        rule.onNodeWithTag("QR-Code Types").assertIsDisplayed()
        rule.onAllNodesWithTag("ContentForm.PageContent").assertCountEquals(0)
        navController.assertCurrentRoute(ContentType.Route)

        // Open the 'ContentForm' page
        rule.onAllNodesWithContentDescription("Qr-Code Type Item")
            .filterToOne(hasText("Text"))
            .performClick()

        // Verify the 'ContentForm' destination
        rule.onNodeWithTag("ContentForm.PageContent").assertIsDisplayed()
        rule.onAllNodesWithTag("QR-Code Types").assertCountEquals(0)
        navController.assertCurrentRoute(ContentForm.RouteWithArgs)

        // Verify the back press navigates to the 'ContentType' page
        Espresso.pressBack()
        rule.onNodeWithTag("QR-Code Types").assertIsDisplayed()
        rule.onAllNodesWithTag("ContentForm.PageContent").assertCountEquals(0)
        navController.assertCurrentRoute(ContentType.Route)

        // Open the 'ContentForm' page
        rule.onAllNodesWithContentDescription("Qr-Code Type Item")
            .filterToOne(hasText("Text"))
            .performClick()

        // Verify the 'ContentForm' destination
        rule.onNodeWithTag("ContentForm.PageContent").assertIsDisplayed()
        rule.onAllNodesWithTag("QR-Code Types").assertCountEquals(0)
        navController.assertCurrentRoute(ContentForm.RouteWithArgs)

        // Input data
        // - Input the title
        rule.onNodeWithTag("ContentForm.Title").performTextInput(Fake.Data.TITLE)
        // - Input the text
        rule.onNodeWithTag("ContentForm.SingleItem").performTextInput(Fake.Data.TEXT)

        // Confirm the form
        rule.onNodeWithTag("ContentForm.ConfirmButton").performClick()

        // Verify the form confirmation navigates to the 'Home' page
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()
        rule.onAllNodesWithTag("ContentForm.PageContent").assertCountEquals(0)
        navController.assertCurrentRoute(Home.Route)
    }

    @Test
    fun showQrCodeToContentForm_navigation_test() {
        val content = Fake.Content.text
        setup(
            Fake.ViewModelBuilder(object : Fake.ContentRepository() {
                override val currentContent = MutableStateFlow<C?>(content)
                override val currentQrCodeType = MutableStateFlow<QrCodeType?>(content.qrCodeType)
                override suspend fun loadContents() = MutableStateFlow(listOf<Content>(content))
            })
        )

        // Verify 'Home' destination
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()
        rule.onAllNodesWithTag("ShowQrCode.Scaffold").assertCountEquals(0)
        navController.assertCurrentRoute(Home.Route)

        // Click on the 'Text' content
        rule.onNodeWithText("Sample Text").performClick()

        // Verify the 'ShowQrCode' destination
        rule.onNodeWithTag("ShowQrCode.Scaffold").assertIsDisplayed()
        rule.onAllNodesWithTag("Qr-Code Contents").assertCountEquals(0)
        navController.assertCurrentRoute(ShowQrCode.Route)

        // Open the 'ContentForm' page in edit mode
        rule.onNodeWithContentDescription("Expand the Toolbox").performClick()
        rule.onNodeWithContentDescription("Edit Content").performClick()

        // Verify the 'ContentForm' page navigation
        rule.onNodeWithTag("ContentForm.PageContent").assertIsDisplayed()
        rule.onAllNodesWithTag("ShowQrCode.Scaffold").assertCountEquals(0)
        navController.assertCurrentRoute(ContentForm.RouteWithArgs)

        // Verify the back press navigates to the 'showQrCode' page
        Espresso.pressBack()
        rule.onNodeWithTag("ShowQrCode.Scaffold").assertIsDisplayed()
        rule.onAllNodesWithTag("ContentForm.PageContent").assertCountEquals(0)
        navController.assertCurrentRoute(ShowQrCode.Route)

        // Open the 'ContentForm' page in edit mode
        rule.onNodeWithContentDescription("Expand the Toolbox").performClick()
        rule.onNodeWithContentDescription("Edit Content").performClick()

        // Verify the 'ContentForm' page navigation
        rule.onNodeWithTag("ContentForm.PageContent").assertIsDisplayed()
        rule.onAllNodesWithTag("ShowQrCode.Scaffold").assertCountEquals(0)
        navController.assertCurrentRoute(ContentForm.RouteWithArgs)

        // Verify the form confirmation navigates to the 'ShowQrCode' page
        // - Input data
        // -- Input the title
        rule.onNodeWithTag("ContentForm.Title").apply {
            assertTextEquals("* Title", "Sample Text")
            performTextClearance()
            performTextInput(Fake.Data.TITLE2)
        }

        // -- Input the text
        rule.onNodeWithTag("ContentForm.SingleItem").apply {
            assertTextEquals("* Text", Fake.Data.TEXT)
            performTextClearance()
            performTextInput(Fake.Data.TEXT2)
        }

        // - Confirm the form
        rule.onNodeWithTag("ContentForm.ConfirmButton").performClick()

        // - Verify the 'ShowQrCode' page
        rule.onNodeWithTag("ShowQrCode.Scaffold").assertIsDisplayed()
        rule.onAllNodesWithTag("ContentForm.PageContent").assertCountEquals(0)
        navController.assertCurrentRoute(ShowQrCode.Route)

        // Remove the content
        rule.onNodeWithContentDescription("Expand the Toolbox").performClick()
        rule.onNodeWithContentDescription("Remove Content").performClick()
        rule.onAllNodesWithText("Remove")
            .filterToOne(hasParent(hasTestTag("Confirmation.Module")))
            .performClick()

        // Verify popping back to 'Home' Page
        rule.onNodeWithTag("Qr-Code Contents").assertIsDisplayed()
        rule.onAllNodesWithTag("ShowQrCode.Scaffold").assertCountEquals(0)
        navController.assertCurrentRoute(Home.Route)
    }

    private fun setup(viewModelBuilder: ViewModelBuilder = Fake.ViewModelBuilder()) {
        MockableMavericks.initialize(app)
        rule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
                AppNav.Host(navController = this, viewModelBuilder = viewModelBuilder)
            }
        }
    }
}
