package com.payamgr.qrcodemaker.view.page.show_qrcode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.payamgr.qrcodemaker.data.model.action.ReactiveAction
import com.payamgr.qrcodemaker.data.model.action.toggle
import com.payamgr.qrcodemaker.data.model.event.ShowQrCodeEvent
import com.payamgr.qrcodemaker.data.model.state.ShowQrCodeState
import com.payamgr.qrcodemaker.view.module.ActionType
import com.payamgr.qrcodemaker.view.module.Confirmation
import com.payamgr.qrcodemaker.view.module.ErrorCorrection
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import com.payamgr.qrcodemaker.view.util.actionOf
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
                    override val effect = flow<ShowQrCodeEvent> {}
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
    const val Route = "show-qrcode"

    fun NavGraphBuilder.showQrCodePage(
        viewModelBuilder: @Composable () -> ShowQrCodeVM,
        onClose: () -> Unit, navigateToContentForm: () -> Unit,
    ) {
        composable(Route) {
            Page(
                viewModel = viewModelBuilder(),
                onClose = onClose,
                navigateToContentForm = { navigateToContentForm() },
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
        HandleEffects(viewModel.effect, onClose, navigateToContentForm)
        val state by viewModel.collectAsState()
        val showConfirmation = remember { mutableStateOf(false) }
        val isFullScreen = remember { mutableStateOf(false) }
        val fullScreenLabel = if (isFullScreen.value) "Full-Screen QR-Code" else "Shrink QR-Code"
        val fullScreenAction = actionOf(isFullScreen)
        state.currentContent?.let { content ->
            Scaffold(
                topBar = { PageAppBar(title = content.title) },
                floatingActionButton = {
                    Toolbox(
                        onRemove = { showConfirmation.value = true },
                        onEdit = viewModel::editContent,
                    )
                },
                modifier = Modifier.testTag("ShowQrCode.Scaffold")
            ) { innerPaddings ->
                BoxWithConstraints(modifier = Modifier.padding(innerPaddings)) {
                    PageContent(
                        isPortrait = maxWidth < maxHeight,
                        qrCode = state.qrCode,
                        text = content.string(),
                        eccAction = ReactiveAction(
                            data = state.ecc,
                            onDataChanged = viewModel::onEccChanged,
                        ),
                        fullScreenAction = fullScreenAction,
                    )
                }
                RemoveConfirmationBottomSheet(
                    showAction = actionOf(showConfirmation),
                    title = content.title,
                    removeContent = viewModel::removeContent,
                )
            }
            FullScreenQrCode(
                qrCode = state.qrCode,
                clickLabel = fullScreenLabel,
                fullScreenAction = fullScreenAction,
            )
        } ?: Text(text = "Invalid Content!")
    }

    @Composable
    fun FullScreenQrCode(qrCode: QrCode, clickLabel: String, fullScreenAction: ReactiveAction<Boolean>) {
        AnimatedVisibility(
            visible = fullScreenAction.data,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Surface(
                color = Color.Black.copy(alpha = .8f),
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClickLabel = clickLabel,
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                        onClick = { fullScreenAction.toggle() }
                    )
                    .testTag("ShowQrCode.FullScreenQrCode")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    QrCodeCanvas(
                        qrCode = qrCode,
                        useMargin = true,
                    )
                }
            }
        }
    }

    @Composable
    fun Toolbox(onRemove: () -> Unit, onEdit: () -> Unit) {
        val expandToolbox = remember { mutableStateOf(false) }
        val hideToolbox = { expandToolbox.value = false }
        Column(modifier = Modifier.testTag("ShowQrCode.Toolbox")) {
            ToolBoxButton(expand = actionOf(expandToolbox))
            AnimatedVisibility(visible = expandToolbox.value) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    RemoveContentButton {
                        onRemove()
                        hideToolbox()
                    }
                    EditContentButton {
                        onEdit()
                        hideToolbox()
                    }
                }
            }
        }
    }

    @Composable
    fun EditContentButton(onClick: () -> Unit) {
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Content",
            )
        }
    }

    @Composable
    fun RemoveContentButton(onClick: () -> Unit) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = Color.White,
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove Content",
            )
        }
    }

    @Composable
    fun ToolBoxButton(expand: ReactiveAction<Boolean>) {
        FloatingActionButton(
            onClick = expand::toggle,
            shape = CircleShape,
        ) {
            Crossfade(targetState = expand.data, label = "Toolbox Icon Cross-fade") { isExpanded ->
                if (isExpanded)
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Collapse the Toolbox",
                    )
                else
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Expand the Toolbox",
                    )
            }
        }
    }

    @Composable
    fun RemoveConfirmationBottomSheet(
        showAction: ReactiveAction<Boolean>,
        title: String,
        removeContent: () -> Unit,
    ) {
        Confirmation.Module(
            showAction,
            title = "Remove Content",
            body = "Are you sure you want to remove \"$title\"?",
            actions = arrayOf(
                ActionType(
                    label = "Remove",
                    called = removeContent,
                    colors = Confirmation.ButtonColors(
                        container = MaterialTheme.colorScheme.errorContainer,
                        text = MaterialTheme.colorScheme.onErrorContainer,
                    )
                )
            ),
        )
    }

    @Composable
    fun HandleEffects(
        effect: Flow<ShowQrCodeEvent>,
        onClose: () -> Unit,
        navigateToContentForm: () -> Unit,
    ) {
        LaunchedEffect(effect) {
            effect.collectLatest {
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
        eccAction: ReactiveAction<ErrorCorrectionCodeLevel>,
        fullScreenAction: ReactiveAction<Boolean>,
    ) {
        if (isPortrait) PortraitContent(
            qrCode = qrCode,
            qrCodeText = text,
            eccAction = eccAction,
            fullScreenAction = fullScreenAction,
        )
        else LandscapeContent(
            qrCode = qrCode,
            qrCodeText = text,
            eccAction = eccAction,
            fullScreenAction = fullScreenAction,
        )
    }

    @Composable
    fun PortraitContent(
        qrCode: QrCode,
        qrCodeText: String,
        eccAction: ReactiveAction<ErrorCorrectionCodeLevel>,
        fullScreenAction: ReactiveAction<Boolean>,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.testTag("ShowQrCode.PortraitContent")
        ) {
            item {
                QrCodeCanvas(
                    qrCode = qrCode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onQrCodeExpandClick(fullScreenAction)
                )
            }
            item {
                ErrorCorrectionLabel()
                ErrorCorrection.Module(eccAction = eccAction)
            }
            item {
                Text(
                    text = qrCodeText,
                    modifier = Modifier.testTag("QR-Code.Text")
                )
            }
        }
    }

    private fun Modifier.onQrCodeExpandClick(fullScreenAction: ReactiveAction<Boolean>) =
        clickable(onClickLabel = "Full-Screen QR-Code") { fullScreenAction.toggle() }

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
        qrCodeText: String,
        eccAction: ReactiveAction<ErrorCorrectionCodeLevel>,
        fullScreenAction: ReactiveAction<Boolean>,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .testTag("ShowQrCode.LandscapeContent"),
        ) {
            QrCodeCanvas(
                qrCode = qrCode,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp)
                    .onQrCodeExpandClick(fullScreenAction),
            )
            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    ErrorCorrectionLabel()
                    ErrorCorrection.Module(eccAction = eccAction)
                }
                item {
                    Text(
                        text = qrCodeText,
                        modifier = Modifier.testTag("QR-Code.Text")
                    )
                }
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.testTag("ShowQrCode.PageAppBar")
        )
    }

    @Composable
    fun QrCodeCanvas(qrCode: QrCode, modifier: Modifier = Modifier, useMargin: Boolean = false) {
        val onBackgroundColor = MaterialTheme.colorScheme.onBackground
        val margin = if (useMargin) LocalDensity.current.run { 16.dp.toPx() } else 0f
        Canvas(
            modifier = modifier
                .aspectRatio(1f)
                .semantics { contentDescription = "QR-Code" },
            onDraw = {
                drawRect(color = Color.Black)
                if (useMargin) {
                    val strokeWidth = 4f
                    withTransform({ this.inset(strokeWidth / 2f) }) {
                        drawRect(color = Color.White, style = Stroke(width = strokeWidth))
                    }
                }
                withTransform({ inset(margin) }) {
                    val n = qrCode.size
                    if (n == 0)
                        drawRect(color = Color.Red)
                    else {
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
                }
            }
        )
    }
}
