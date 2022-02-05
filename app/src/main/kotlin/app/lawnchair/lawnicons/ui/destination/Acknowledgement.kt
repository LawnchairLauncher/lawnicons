package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.lawnchair.lawnicons.ui.component.ClickableIcon
import app.lawnchair.lawnicons.ui.component.TopBarWithInsets
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation
import app.lawnchair.lawnicons.viewmodel.AcknowledgementViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Acknowledgement(
    name: String?,
    acknowledgementViewModel: AcknowledgementViewModel = hiltViewModel(),
    navController: NavController,
) {
    requireNotNull(name)

    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
    val density = LocalDensity.current
    val navigationBarHeight = with(density) {
        LocalWindowInsets.current.navigationBars.bottom.toDp()
    }

    val notice by acknowledgementViewModel.getNoticeForOssLibrary(
        ossLibraryName = name,
        linkStyle = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
        ),
    ).collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBarWithInsets(
                scrollBehavior = scrollBehavior,
                title = name,
                navigationIcon = {
                    ClickableIcon(
                        onClick = { navController.popBackStack() },
                        imageVector = Icons.Rounded.ArrowBack,
                        size = 40.dp,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                },
            )
        },
    ) { innerPadding ->
        Crossfade(
            targetState = notice,
            modifier = Modifier.padding(innerPadding),
        ) {
            Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (it != null) {
                    val uriHandler = LocalUriHandler.current
                    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

                    val clickHandler = Modifier.pointerInput(Unit) {
                        detectTapGestures { offset ->
                            textLayoutResult?.let { layoutResult ->
                                val position = layoutResult.getOffsetForPosition(offset)
                                val annotation = it.getStringAnnotations(
                                    start = position,
                                    end = position,
                                ).firstOrNull()

                                if (annotation?.tag == "URL") {
                                    uriHandler.openUri(annotation.item)
                                }
                            }
                        }
                    }

                    Text(
                        text = it,
                        fontFamily = FontFamily.Monospace,
                        modifier = clickHandler.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = navigationBarHeight,
                        ),
                        onTextLayout = { result ->
                            textLayoutResult = result
                        },
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(2) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .placeholder(
                                        visible = true,
                                        highlight = PlaceholderHighlight.fade(),
                                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                            Elevation.Level2
                                        ),
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
