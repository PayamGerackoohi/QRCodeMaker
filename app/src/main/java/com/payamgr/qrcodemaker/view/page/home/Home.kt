package com.payamgr.qrcodemaker.view.page.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.payamgr.qrcodemaker.data.database.entity.TextContent
import com.payamgr.qrcodemaker.data.model.Content
import com.payamgr.qrcodemaker.data.model.event.HomeEvent
import com.payamgr.qrcodemaker.data.model.state.HomeState
import com.payamgr.qrcodemaker.view.theme.QRCodeMakerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@Preview
@Composable
fun HomePage_Preview() {
    QRCodeMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Mavericks.initialize(LocalContext.current)
            val state = HomeState(contents = (1..3).map { TextContent("$it".repeat(5), "") })
            Home.Page(
                navigateToContentType = {},
                navigateToShowQrCode = {},
                viewModel = object : HomeVM(state) {
                    override val eventFlow: Flow<HomeEvent> get() = flowOf()
                    override fun showQrCode(content: Content) {}
                    override fun showContentTypePage() {}
                })
        }
    }
}

object Home {
    const val Route = "home"

    fun NavGraphBuilder.homePage(navigateToContentType: () -> Unit, navigateToShowQrCode: (Content) -> Unit) {
        composable(Route) {
            Page(navigateToContentType, navigateToShowQrCode)
        }
    }

    @Composable
    fun Page(
        navigateToContentType: () -> Unit,
        navigateToShowQrCode: (Content) -> Unit,
        viewModel: HomeVM = mavericksViewModel(),
    ) {
        HandleEvents(viewModel.eventFlow, navigateToShowQrCode, navigateToContentType)
        val state by viewModel.collectAsState()
        Scaffold(
            floatingActionButton = { AddContentButton(onClick = viewModel::showContentTypePage) },
        ) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(250.dp),
                contentPadding = PaddingValues(6.dp),
                modifier = Modifier.padding(innerPadding)
            ) {
                items(state.contents) {
                    QrCodeContent(
                        content = it,
                        onClick = { viewModel.showQrCode(it) },
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }

    @Composable
    fun AddContentButton(onClick: () -> Unit) {
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Content")
        }
    }

    @Composable
    fun HandleEvents(
        event: Flow<HomeEvent>,
        navigateToShowQrCode: (Content) -> Unit,
        navigateToContentType: () -> Unit,
    ) {
        LaunchedEffect(key1 = Unit) {
            event.collectLatest {
                when (it) {
                    is HomeEvent.NavigateToShowQrCode -> navigateToShowQrCode(it.content)
                    HomeEvent.NavigateToContentType -> navigateToContentType()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun QrCodeContent(content: Content, onClick: () -> Unit, modifier: Modifier = Modifier) {
        Card(
            colors = CardDefaults.cardColors(contentColor = MaterialTheme.colorScheme.secondaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = content.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    text = stringResource(content.qrCodeType.titleId),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
