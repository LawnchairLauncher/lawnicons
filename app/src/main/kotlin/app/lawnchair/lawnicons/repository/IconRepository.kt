package app.lawnchair.lawnicons.repository

import android.app.Application
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.IconInfoManager
import app.lawnchair.lawnicons.model.IconInfoModel
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.model.SearchInfo
import app.lawnchair.lawnicons.model.SearchMode
import app.lawnchair.lawnicons.model.getFirstLabelAndComponent
import app.lawnchair.lawnicons.util.getIconInfo
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

    private var iconInfo: List<IconInfo> = emptyList()
    val iconInfoModel = MutableStateFlow<IconInfoModel?>(value = null)
    val searchedIconInfoModel = MutableStateFlow<IconInfoModel?>(value = null)
    private var systemPackageList: List<IconInfo>? = null
    var iconRequestList = MutableStateFlow<IconRequestModel?>(value = null)

    private var iconCount = 0

    init {
        coroutineScope.launch {
            application.getIconInfo()
                .also { list ->
                    iconInfo = list.sortedBy { it.label.lowercase() }
                }
                .associateBy { it.label }.values
                .also {
                    iconCount = it.size
                }

            iconInfoModel.value = IconInfoModel(
                iconInfo = iconInfo.toPersistentList(),
                iconCount = iconCount,
            )
            searchedIconInfoModel.value = IconInfoModel(
                iconInfo = IconInfoManager.splitByComponentName(iconInfo).toPersistentList(),
                iconCount = iconCount,
            )

            systemPackageList = application.getSystemIconInfoAppfilter()
                .associateBy { it.label }.values
                .sortedBy { it.label.lowercase() }

            getIconRequestList()
        }
    }

    suspend fun search(
        mode: SearchMode,
        query: String,
    ) = withContext(Dispatchers.Default) {
        searchedIconInfoModel.value = IconInfoManager
            .splitByComponentName(iconInfo)
            .let {
                val filtered = it.mapNotNull { candidate ->
                    val searchIn =
                        when (mode) {
                            SearchMode.LABEL -> candidate.getFirstLabelAndComponent().label
                            SearchMode.COMPONENT -> candidate.getFirstLabelAndComponent().componentName
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
        searchedIconInfoModel.value = iconInfoModel.value?.let {
            IconInfoModel(
                iconCount = it.iconCount,
                iconInfo = IconInfoManager.splitByComponentName(iconInfo).toPersistentList(),
            )
        }
    }

    private suspend fun getIconRequestList() = withContext(Dispatchers.Default) {
        iconRequestList.value = systemPackageList?.let { packageList ->
            val lawniconsData = iconInfo

            val systemData = packageList.map { info ->
                info.getFirstLabelAndComponent().also {
                    IconRequest(
                        it.label,
                        it.componentName,
                    )
                }
            }

            val lawniconsComponents = IconInfoManager.splitByComponentName(lawniconsData)
                .map { it.getFirstLabelAndComponent().componentName }
                .sortedBy { it.lowercase() }
                .toSet()

            val commonItems = systemData.filter { it.componentName !in lawniconsComponents }.map {
                IconRequest(
                    it.label,
                    it.componentName,
                )
            }

            IconRequestModel(
                list = commonItems.toImmutableList(),
                iconCount = commonItems.size,
            )
        }
    }
}
