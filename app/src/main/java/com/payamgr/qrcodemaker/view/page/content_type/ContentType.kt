package com.payamgr.qrcodemaker.view.page.content_type

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.payamgr.qrcodemaker.data.model.QrCodeType
import com.payamgr.qrcodemaker.data.model.event.ContentTypeEffect
import com.payamgr.qrcodemaker.data.model.state.ContentTypeState
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@Preview
@Composable
fun ContentTypePage_Preview() {
    QRCodeMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Mavericks.initialize(LocalContext.current)
            ContentType.Page(
                navigateToContentForm = {},
                viewModel = object : ContentTypeVM(
                    ContentTypeState(
                        qrCodeTypes = listOf(
                            QrCodeType.Text(),
                            QrCodeType.PhoneCall(),
                            QrCodeType.MeCard(),
                        )
                    )
                ) {
                    override val effect: Flow<ContentTypeEffect> get() = flowOf()
                    override fun showContentForm(type: QrCodeType) {}
                },
            )
        }
    }
}

object ContentType {
    const val Route = "content-type"

    fun NavGraphBuilder.contentTypePage(
        viewModelBuilder: @Composable () -> ContentTypeVM,
        navigateToContentForm: () -> Unit,
    ) {
        composable(Route) {
            Page(navigateToContentForm = { navigateToContentForm() }, viewModel = viewModelBuilder())
        }
    }

    fun NavHostController.navigateToContentType() = navigate(Route) { launchSingleTop = true }

    @Composable
    fun Page(viewModel: ContentTypeVM = mavericksViewModel(), navigateToContentForm: () -> Unit) {
        HandleEffects(viewModel.effect, navigateToContentForm)
        val state by viewModel.collectAsState()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(250.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.testTag("QR-Code Types")
        ) {
            items(state.qrCodeTypes) {
                QrCodeTypeItem(
                    titleId = it.titleId,
                    onItemClicked = { viewModel.showContentForm(it) },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    @Composable
    fun QrCodeTypeItem(
        @StringRes titleId: Int,
        onItemClicked: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Button(
            onClick = { onItemClicked() },
            contentPadding = PaddingValues(24.dp),
            modifier = modifier.semantics { contentDescription = "Qr-Code Type Item" },
        ) {
            Text(text = stringResource(titleId))
        }
    }

    @Composable
    fun HandleEffects(effect: Flow<ContentTypeEffect>, navigateToContentForm: () -> Unit) {
        LaunchedEffect(effect) {
            effect.collectLatest {
                when (it) {
                    is ContentTypeEffect.NavigateToContentForm -> navigateToContentForm()
                }
            }
        }
    }
}
