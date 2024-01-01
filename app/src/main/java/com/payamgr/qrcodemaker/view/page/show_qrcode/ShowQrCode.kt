package com.payamgr.qrcodemaker.view.page.show_qrcode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.payamgr.qrcodemaker.data.database.entity.TextContent
import com.payamgr.qrcodemaker.data.model.ErrorCorrectionCodeLevel
import com.payamgr.qrcodemaker.data.model.QrCode
import com.payamgr.qrcodemaker.data.model.event.ShowQrCodeEvent
import com.payamgr.qrcodemaker.data.model.state.ShowQrCodeState
import com.payamgr.qrcodemaker.view.module.ActionType
import com.payamgr.qrcodemaker.view.module.Confirmation
import com.payamgr.qrcodemaker.view.module.ErrorCorrectionModule
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

@Preview
@Composable
fun ShowQrCodePage_Preview() {
    QRCodeMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Mavericks.initialize(LocalContext.current)
            val state = ShowQrCodeState(TextContent("Title", "\nText".repeat(10)))
            ShowQrCode.Page(
                viewModel = object : ShowQrCodeVM(state) {
                    override val eventFlow = flow<ShowQrCodeEvent> {}
                    override fun removeContent() {}
                    override fun editContent() {}
                    override fun onEccChanged(ecc: ErrorCorrectionCodeLevel) {}
                },
                onClose = {},
                navigateToContentForm = {},
            )
        }
    }
}

object ShowQrCode {
    private const val Route = "show-qrcode"

    fun NavGraphBuilder.showQrCodePage(onClose: () -> Unit, navigateToContentForm: (isEditMode: Boolean) -> Unit) {
        composable(Route) {
            Page(
                onClose = onClose,
                navigateToContentForm = { navigateToContentForm(true) },
            )
        }
    }

    fun NavHostController.navigateToShowQrCode() = navigate(Route) { launchSingleTop = true }

    @Composable
    fun Page(
        viewModel: ShowQrCodeVM = mavericksViewModel(),
        onClose: () -> Unit,
        navigateToContentForm: () -> Unit,
    ) {
        HandleEvents(viewModel.eventFlow, onClose, navigateToContentForm)
        val state by viewModel.collectAsState()
        var showConfirmation by remember { mutableStateOf(false) }
        state.currentContent?.let { content ->
            Scaffold(
                topBar = { PageAppBar(title = content.title) },
                floatingActionButton = {
                    Toolbox(
                        onRemove = { showConfirmation = true },
                        onEdit = viewModel::editContent,
                    )
                },
            ) {
                BoxWithConstraints(modifier = Modifier.padding(it)) {
                    PageContent(
                        isPortrait = maxWidth < maxHeight,
                        qrCode = state.qrCode,
                        text = content.print(),
                        ecc = state.ecc,
                        onEccChanged = viewModel::onEccChanged,
                    )
                    RemoveConfirmationBottomSheet(
                        show = showConfirmation,
                        onDismiss = { showConfirmation = false },
                        title = content.title,
                        removeContent = viewModel::removeContent,
                    )
                }
            }
        } ?: Text(text = "Invalid Content!")
    }

    @Composable
    fun Toolbox(onRemove: () -> Unit, onEdit: () -> Unit) {
        var expandToolbox by remember { mutableStateOf(false) }
        Column {
            FloatingActionButton(
                onClick = { expandToolbox = expandToolbox.not() },
                shape = CircleShape,
            ) {
                Crossfade(targetState = expandToolbox, label = "Toolbox Icon Cross-fade") { isExpanded ->
                    if (isExpanded)
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Toggle Toolbox",
                        )
                    else
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Toggle Toolbox",
                        )
                }
            }
            AnimatedVisibility(visible = expandToolbox) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    FloatingActionButton(
                        onClick = onRemove,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = Color.White,
                        shape = CircleShape,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Content",
                        )
                    }
                    FloatingActionButton(
                        onClick = onEdit,
                        shape = CircleShape,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Content",
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun RemoveConfirmationBottomSheet(
        show: Boolean,
        onDismiss: () -> Unit,
        title: String,
        removeContent: () -> Unit,
    ) {
        Confirmation.Module(
            show = show,
            onDismiss = onDismiss,
            title = "Remove Content",
            body = "Are you sure you want to remove \"$title\"?",
            actions = arrayOf(
                ActionType(
                    id = 0,
                    label = "Remove",
                    colors = Confirmation.ButtonColors(
                        container = MaterialTheme.colorScheme.errorContainer,
                        text = MaterialTheme.colorScheme.onErrorContainer,
                    )
                )
            ),
            onAction = { result -> if (result == 0) removeContent() },
        )
    }

    @Composable
    fun HandleEvents(
        event: Flow<ShowQrCodeEvent>,
        onClose: () -> Unit,
        navigateToContentForm: () -> Unit,
    ) {
        LaunchedEffect(key1 = Unit) {
            event.collectLatest {
                when (it) {
                    ShowQrCodeEvent.ClosePage -> onClose()
                    ShowQrCodeEvent.NavigateToContentForm -> navigateToContentForm()
                }
            }
        }
    }

    @Composable
    fun PageContent(
        isPortrait: Boolean,
        qrCode: QrCode,
        text: String,
        ecc: ErrorCorrectionCodeLevel,
        onEccChanged: (ErrorCorrectionCodeLevel) -> Unit,
    ) {
        if (isPortrait) PortraitContent(
            qrCode = qrCode,
            text = text,
            ecc = ecc,
            onEccChanged = onEccChanged
        )
        else LandscapeContent(
            qrCode = qrCode,
            text = text,
            level = ecc,
            onLevelChanged = onEccChanged
        )
    }

    @Composable
    fun PortraitContent(
        qrCode: QrCode,
        text: String,
        ecc: ErrorCorrectionCodeLevel,
        onEccChanged: (ErrorCorrectionCodeLevel) -> Unit,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                QrCodeCanvas(
                    qrCode = qrCode,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                ErrorCorrectionLabel()
                ErrorCorrectionModule(
                    currentEcc = ecc,
                    onEccChanged = onEccChanged,
                )
            }
            item { Text(text = text) }
        }
    }

    @Composable
    fun ErrorCorrectionLabel() {
        Text(
            text = "Error Correction Code Level",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    fun LandscapeContent(
        qrCode: QrCode,
        text: String,
        level: ErrorCorrectionCodeLevel,
        onLevelChanged: (ErrorCorrectionCodeLevel) -> Unit,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            QrCodeCanvas(
                qrCode = qrCode,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp),
            )
            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    ErrorCorrectionLabel()
                    ErrorCorrectionModule(
                        currentEcc = level,
                        onEccChanged = onLevelChanged,
                    )
                }
                item { Text(text = text) }
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        )
    }

    @Composable
    fun QrCodeCanvas(qrCode: QrCode, modifier: Modifier = Modifier) {
        val onBackgroundColor = MaterialTheme.colorScheme.onBackground
        Canvas(modifier = modifier.aspectRatio(1f), onDraw = {
            val n = qrCode.size
            if (n == 0) {
                drawRect(color = Color.Red, style = Stroke(width = 2f))
            } else {
                val s = size.minDimension / n
                repeat(n) { i ->
                    repeat(n) { j ->
                        if (qrCode.data[i * n + j]) {
                            val topLeft = Offset(i * s, j * s)
                            val size = Size(s, s)
                            drawRect(
                                color = onBackgroundColor,
                                topLeft = topLeft,
                                size = size,
                            )
                            drawRect(
                                color = onBackgroundColor,
                                topLeft = topLeft,
                                size = size,
                                style = Stroke(width = 1f),
                            )
                        }
                    }
                }
            }
        })
    }
}
