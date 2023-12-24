package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.components.core.placeholder.PlaceholderHighlight
import app.lawnchair.lawnicons.ui.components.core.placeholder.material.fade
import app.lawnchair.lawnicons.ui.components.core.placeholder.material.placeholder
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation
import app.lawnchair.lawnicons.viewmodel.AcknowledgementViewModel

@Composable
fun Acknowledgement(
    name: String?,
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
    acknowledgementViewModel: AcknowledgementViewModel = hiltViewModel(),
) {
    requireNotNull(name)

    val notice by acknowledgementViewModel.getNoticeForOssLibrary(
        ossLibraryName = name,
        linkStyle = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
        ),
    ).collectAsState()

    LawniconsScaffold(
        title = name,
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
    ) { innerPadding ->
        Crossfade(
            targetState = notice,
            modifier = Modifier.padding(innerPadding),
            label = "",
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
                        modifier = clickHandler
                            .padding(horizontal = 16.dp)
                            .navigationBarsPadding(),
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
                                            Elevation.Level2,
                                        ),
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}
