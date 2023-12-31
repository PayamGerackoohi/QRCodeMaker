package com.payamgr.qrcodemaker.view.page.content_form

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.payamgr.qrcodemaker.R
import com.payamgr.qrcodemaker.data.database.entity.MeCardContent
import com.payamgr.qrcodemaker.data.database.entity.PhoneCallContent
import com.payamgr.qrcodemaker.data.database.entity.TextContent
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.Input
import com.payamgr.qrcodemaker.data.model.InputData
import com.payamgr.qrcodemaker.data.model.InputId
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.model.event.ContentFormEvent
import com.payamgr.qrcodemaker.view.module.InputModule
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Preview
@Composable
fun ContentFormPage_Preview() {
    QRCodeMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Mavericks.initialize(LocalContext.current)
            ContentForm.Page(onClose = {})
        }
    }
}

object ContentForm {
    private const val Route = "content-form"
    private const val editModeArg = "is-edit-mode"

    fun NavGraphBuilder.contentFormPage(onClose: (isEditMode: Boolean) -> Unit) {
        composable(
            route = "$Route/{$editModeArg}",
            arguments = listOf(navArgument(editModeArg) { type = NavType.BoolType }),
        ) { Page(onClose = onClose, isEditMode = it.arguments?.getBoolean(editModeArg) == true) }
    }

    fun NavHostController.navigateToContentForm(isEditMode: Boolean) = navigate("$Route/$isEditMode") {
        launchSingleTop = true
    }

    @Composable
    fun Page(
        viewModel: ContentFormVM = mavericksViewModel(),
        onClose: (isEditMode: Boolean) -> Unit,
        isEditMode: Boolean = false,
    ) {
        HandleEvents(viewModel.eventFlow, onClose = { onClose(isEditMode) })
        val state by viewModel.collectAsState()
        state.currentQrCodeType?.let { qrCodeType ->
            val snackbarState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            val showSnackbar: (String) -> Unit = { scope.launch { snackbarState.showSnackbar(it) } }
            Scaffold(
                topBar = { PageAppBar(title = stringResource(qrCodeType.titleId)) },
                snackbarHost = { SnackbarHost(hostState = snackbarState) }
            ) { paddingValues ->
                PageContent(
                    paddingValues = paddingValues,
                    qrCodeType = qrCodeType,
                    contentTitle = if (isEditMode) state.contentTitle else "",
                    add = { if (isEditMode) viewModel.update(it) else viewModel.add(it) },
                    showSnackbar = showSnackbar
                )
            }
        } ?: Text(text = "No currentQrCodeType!")
    }

    @Composable
    fun HandleEvents(event: Flow<ContentFormEvent>, onClose: () -> Unit) {
        LaunchedEffect(key1 = Unit) {
            event.collectLatest {
                when (it) {
                    ContentFormEvent.ClosePage -> onClose()
                }
            }
        }
    }

    @Composable
    fun PageContent(
        paddingValues: PaddingValues,
        qrCodeType: QrCodeType,
        contentTitle: String,
        add: (Content) -> Unit,
        showSnackbar: (String) -> Unit,
    ) {
        val inputMap = remember { mutableMapOf<InputId, InputData>() }
        val addInput: (InputId, InputData) -> Unit = { id, data -> inputMap[id] = data }
        var showValidation by remember { mutableStateOf(false) }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { Description(qrCodeType.descriptionId) }
            item { Title(showValidation, contentTitle, addInput) }
            items(qrCodeType.inputs) { input -> InputItem(input, addInput, showValidation) }
            item {
                ConfirmButton(
                    qrCodeType = qrCodeType,
                    areInputsValid = { inputMap.values.all { it.isValid } },
                    onShowValidationChanged = { showValidation = it },
                    inputMap = inputMap,
                    addContent = add,
                    showSnackbar = { showSnackbar("Invalid Input") },
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PageAppBar(title: String) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }

    @Composable
    fun Description(@StringRes descriptionId: Int) {
        Text(text = stringResource(descriptionId))
    }

    @Composable
    fun Title(showValidation: Boolean, contentTitle: String, addInput: (InputId, InputData) -> Unit) {
        SingleItem(
            input = Input.Single(InputId.Title, R.string.title, contentTitle, KeyboardType.Text),
            showValidation = showValidation,
            addInput = addInput,
        )
    }

    @Composable
    fun InputItem(
        input: Input,
        addInput: (InputId, InputData) -> Unit,
        showValidation: Boolean,
    ) {
        when (input) {
            is Input.Single -> SingleItem(input, showValidation, addInput)
            is Input.Group -> GroupItem(input, showValidation, addInput)
        }
    }

    @Composable
    fun SingleItem(
        input: Input.Single,
        showValidation: Boolean,
        addInput: (InputId, InputData) -> Unit,
    ) {
        var text by remember(input.initialValue) { mutableStateOf(input.initialValue) }
        val data = remember(text) {
            InputData(text, input.isOptional).apply {
                addInput(input.id, this)
            }
        }
        InputModule.TextType(
            value = text,
            onValueChange = { text = it },
            label = stringResource(id = input.labelId),
            keyboardType = input.keyboardType,
            isMandatory = !input.isOptional,
            singleLine = input.singleLine,
            isError = showValidation && !data.isValid,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    @Composable
    fun GroupItem(group: Input.Group, showValidation: Boolean, addInput: (InputId, InputData) -> Unit) {
        Card {
            Text(
                text = stringResource(group.titleId),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .padding(16.dp)
            )
            Column {
                group.inputs.forEach { input ->
                    SingleItem(
                        input = input,
                        showValidation = showValidation,
                        addInput = addInput
                    )
                }
            }
        }
    }

    @Composable
    fun ConfirmButton(
        qrCodeType: QrCodeType,
        inputMap: Map<InputId, InputData>,
        areInputsValid: () -> Boolean,
        onShowValidationChanged: (Boolean) -> Unit,
        addContent: (Content) -> Unit,
        showSnackbar: (String) -> Unit,
    ) {
        Button(onClick = {
            onShowValidationChanged(true)
            if (areInputsValid()) {
                val content: Content = when (qrCodeType) {
                    is QrCodeType.Text -> TextContent.digest(inputMap)
                    is QrCodeType.PhoneCall -> PhoneCallContent.digest(inputMap)
                    is QrCodeType.MeCard -> MeCardContent.digest(inputMap)
                }
                addContent(content)
            } else
                showSnackbar("Invalid Input")
        }) {
            Text(text = "Confirm")
        }
    }
}
