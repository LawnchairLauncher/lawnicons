package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.components.home.ClickableIcon
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.toPaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawniconsScaffold(
    title: String,
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
    content: @Composable (PaddingValues) -> Unit,
) {
    var scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    if (isExpandedScreen) {
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = title,
                navigationIcon = {
                    ClickableIcon(
                        onClick = onBack,
                        imageVector = Icons.Rounded.ArrowBack,
                        size = 40.dp,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                },
                isExpandedScreen = isExpandedScreen,
            )
        },
    ) {
        Box(
            modifier = Modifier.then(
                if (isExpandedScreen) {
                    Modifier.padding(
                        WindowInsets.navigationBars.toPaddingValues(
                            additionalStart = 32.dp,
                            additionalEnd = 32.dp,
                        ),
                    )
                } else {
                    Modifier
                },
            ),
        ) { content(it) }
    }
}

@PreviewLawnicons
@Composable
private fun LawniconsScaffoldPreview() {
    LawniconsTheme {
        LawniconsScaffold(
            title = "Example small bar",
            onBack = { },
            isExpandedScreen = false,
            content = {
                Box(
                    modifier = Modifier.padding(it),
                ) {
                    Text("Hello World")
                }
            },
        )
    }
}

@PreviewLawnicons
@Composable
private fun LawniconsScaffoldExpandedPreview() {
    LawniconsTheme {
        LawniconsScaffold(
            title = "Example small bar",
            onBack = { },
            isExpandedScreen = true,
            content = {
                Box(
                    modifier = Modifier.padding(it),
                ) {
                    Text("Hello World")
                }
            },
        )
    }
}
