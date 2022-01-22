package app.lawnchair.lawnicons

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class IconRepository @Inject constructor(application: Application) {

    private var _iconInfo: List<IconInfo>? = null
    val iconInfoModel = MutableStateFlow<IconInfoModel?>(value = null)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        coroutineScope.launch {
            _iconInfo = application.getIconInfo()
                .associateBy { it.name }.values
                .sortedBy { it.name.lowercase() }
                .also {
                    iconInfoModel.value = IconInfoModel(
                        iconInfo = it,
                        iconCount = it.size,
                    )
                }
        }
    }

    fun search(query: String) {
        coroutineScope.launch {
            iconInfoModel.value = _iconInfo?.let {
                IconInfoModel(
                    iconCount = it.size,
                    iconInfo = it.filter { candidate ->
                        candidate.name.lowercase().contains(query.lowercase())
                    },
                )
            }
        }
    }
}
