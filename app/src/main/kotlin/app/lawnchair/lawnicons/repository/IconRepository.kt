package app.lawnchair.lawnicons.repository

import android.app.Application
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.IconInfoModel
import app.lawnchair.lawnicons.model.SearchInfo
import app.lawnchair.lawnicons.util.getIconInfo
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IconRepository @Inject constructor(application: Application) {

    private var iconInfo: List<IconInfo>? = null
    val iconInfoModel = MutableStateFlow<IconInfoModel?>(value = null)
    val searchedIconInfoModel = MutableStateFlow<IconInfoModel?>(value = null)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        coroutineScope.launch {
            iconInfo = application.getIconInfo()
                .associateBy { it.name }.values
                .sortedBy { it.name.lowercase() }
                .also {
                    iconInfoModel.value = IconInfoModel(
                        iconInfo = it.toPersistentList(),
                        iconCount = it.size,
                    )
                    searchedIconInfoModel.value = IconInfoModel(
                        iconInfo = it.toPersistentList(),
                        iconCount = it.size,
                    )
                }
        }
    }

    suspend fun search(query: String) = withContext(Dispatchers.Default) {
        searchedIconInfoModel.value = iconInfo?.let {
            val filtered = it.mapNotNull { candidate ->
                val indexOfMatch =
                    candidate.name.indexOf(string = query, ignoreCase = true).also { index ->
                        if (index == -1) return@mapNotNull null
                    }
                val matchAtWordStart = indexOfMatch == 0 || candidate.name[indexOfMatch - 1] == ' '
                SearchInfo(
                    iconInfo = candidate,
                    indexOfMatch = indexOfMatch,
                    matchAtWordStart = matchAtWordStart,
                )
            }.sortedWith(
                compareBy(
                    { searchInfo -> !searchInfo.matchAtWordStart },
                    { searchInfo -> searchInfo.indexOfMatch },
                ),
            ).map { searchInfo ->
                searchInfo.iconInfo
            }.toPersistentList()
            IconInfoModel(
                iconCount = it.size,
                iconInfo = filtered,
            )
        }
    }
}
