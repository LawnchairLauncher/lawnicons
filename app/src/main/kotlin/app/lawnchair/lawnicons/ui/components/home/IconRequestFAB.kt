package app.lawnchair.lawnicons.ui.components.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.ui.util.isScrollingUp

@Composable
fun IconRequestFAB(
    iconRequestModel: IconRequestModel?,
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    Log.d("IconRequestFAB", "iconRequestModel: $iconRequestModel")
    if (iconRequestModel != null) {
        if (iconRequestModel.iconCount > 0) {
            IconRequestFAB(
                iconRequestList = iconRequestModel.list,
                lazyGridState = lazyGridState,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun IconRequestFAB(
    iconRequestList: List<IconRequest>,
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val request = buildForm(iconRequestList.joinToString("%0A") { "${it.name}%0A${it.componentName}" })

    ExtendedFloatingActionButton(
        text = {
            Text(stringResource(R.string.unthemed_icons_info))
        },
        icon = { Icon(painter = painterResource(id = R.drawable.icon_request_app), contentDescription = null) },
        onClick = {
            val website = Uri.parse(request)
            val intent = Intent(Intent.ACTION_VIEW, website)
            context.startActivity(intent)
        },
        modifier = modifier,
        expanded = lazyGridState.isScrollingUp(),
    )
}

private fun buildForm(string: String): String {
    return "https://docs.google.com/forms/d/e/1FAIpQLSe8ItNYse9f4z2aT1QgXkKeueVTucRdUYNhUpys5ShHPyRijg/viewform?entry.1759726669=$string"
}
