package app.lawnchair.lawnicons.repository

import android.app.Application
import app.lawnchair.lawnicons.model.IconInfoAppfilter
import app.lawnchair.lawnicons.model.IconInfoModel
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.model.SearchInfo
import app.lawnchair.lawnicons.model.SearchMode
import app.lawnchair.lawnicons.util.getIconInfoAppfilter
import app.lawnchair.lawnicons.util.getSystemIconInfoAppfilter
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IconRepository @Inject constructor(application: Application) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var iconInfo: List<IconInfoAppfilter>? = null
    val iconInfoModel = MutableStateFlow<IconInfoModel?>(value = null)
    val searchedIconInfoModel = MutableStateFlow<IconInfoModel?>(value = null)

    private var lawniconsData: List<IconInfoAppfilter>? = null

    private var systemPackageList: List<IconInfoAppfilter>? = null
    var iconRequestList = MutableStateFlow<IconRequestModel?>(value = null)

    init {
        coroutineScope.launch {
            iconInfo = application.getIconInfoAppfilter()
                .also { list ->
                    lawniconsData = list.sortedBy { it.name.lowercase() }
                }
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

            systemPackageList = application.getSystemIconInfoAppfilter()
                .associateBy { it.name }.values
                .sortedBy { it.name.lowercase() }
            getIconRequestList()
        }
    }

    suspend fun search(
        mode: SearchMode,
        query: String,
    ) = withContext(Dispatchers.Default) {
        searchedIconInfoModel.value = iconInfo?.let {
            val filtered = it.mapNotNull { candidate ->
                val searchIn =
                    when (mode) {
                        SearchMode.NAME -> candidate.name
                        SearchMode.PACKAGE_NAME -> candidate.componentName
                        SearchMode.DRAWABLE -> candidate.drawableName
                    }
                val indexOfMatch =
                    searchIn.indexOf(string = query, ignoreCase = true).also { index ->
                        if (index == -1) return@mapNotNull null
                    }
                val matchAtWordStart = indexOfMatch == 0 || searchIn[indexOfMatch - 1] == ' '
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

    fun clear() {
        searchedIconInfoModel.value = iconInfoModel.value
    }

    private suspend fun getIconRequestList() = withContext(Dispatchers.Default) {
        iconRequestList.value = systemPackageList?.let { packageList ->
            val lawniconsData = lawniconsData ?: emptyList()

            val systemData = packageList.map {
                IconRequest(
                    it.name,
                    it.componentName,
                )
            }

            val lawniconsComponents = lawniconsData
                .map { it.componentName }
                .sortedBy { it.lowercase() }
                .toSet()

            val commonItems = systemData.filter { it.componentName !in lawniconsComponents }

            IconRequestModel(
                list = commonItems.toImmutableList(),
                iconCount = commonItems.size,
            )
        }
    }
}
