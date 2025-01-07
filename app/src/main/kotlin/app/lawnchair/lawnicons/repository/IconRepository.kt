package app.lawnchair.lawnicons.repository

import android.app.Application
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.IconInfoModel
import app.lawnchair.lawnicons.model.IconRequest
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.model.SearchInfo
import app.lawnchair.lawnicons.model.SearchMode
import app.lawnchair.lawnicons.model.getFirstLabelAndComponent
import app.lawnchair.lawnicons.model.splitByComponentName
import app.lawnchair.lawnicons.util.getIconInfo
import app.lawnchair.lawnicons.util.getSystemIconInfoAppfilter
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface IconRepository {
    val iconInfoModel: StateFlow<IconInfoModel>
    val searchedIconInfoModel: StateFlow<IconInfoModel>
    val iconRequestList: MutableStateFlow<IconRequestModel?>

    suspend fun search(mode: SearchMode, query: String)
    fun clearSearch()
}

class IconRepositoryImpl @Inject constructor(application: Application) : IconRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _iconInfoModel = MutableStateFlow(IconInfoModel())
    override val iconInfoModel = _iconInfoModel.asStateFlow()

    private val _searchedIconInfoModel = MutableStateFlow(IconInfoModel())
    override val searchedIconInfoModel = _searchedIconInfoModel.asStateFlow()

    override val iconRequestList = MutableStateFlow<IconRequestModel?>(value = null)

    init {
        coroutineScope.launch {
            val iconList = application.getIconInfo().sortedBy { it.label.lowercase() }
            val groupedIcons = iconList.associateBy { it.label }.values
            val iconCount = groupedIcons.size

            _iconInfoModel.value = IconInfoModel(
                iconInfo = iconList,
                iconCount = iconCount,
            )
            _searchedIconInfoModel.value = _iconInfoModel.value

            val systemPackageList = application.getSystemIconInfoAppfilter()
                .associateBy { it.label }.values
                .sortedBy { it.label.lowercase() }
            getIconRequestList(systemPackageList)
        }
    }

    override suspend fun search(
        mode: SearchMode,
        query: String,
    ) = withContext(Dispatchers.Default) {
        val filteredIcons = _iconInfoModel.value.iconInfo.mapNotNull { candidate ->
            val searchIn = when (mode) {
                SearchMode.LABEL -> candidate.componentNames.map { it.label }
                SearchMode.COMPONENT -> candidate.componentNames.map { it.componentName }
                SearchMode.DRAWABLE -> listOf(candidate.drawableName)
            }
            val indexOfMatch = searchIn.map {
                it.indexOf(string = query, ignoreCase = true)
            }.filter { it != -1 }.minOrNull() ?: return@mapNotNull null
            val matchAtWordStart = searchIn.any {
                it.indexOf(string = query, ignoreCase = true) == 0 ||
                    it.getOrNull(it.indexOf(string = query, ignoreCase = true) - 1) == ' '
            }
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
        }

        _searchedIconInfoModel.value = IconInfoModel(
            iconCount = _searchedIconInfoModel.value.iconCount,
            iconInfo = filteredIcons,
        )
    }

    override fun clearSearch() {
        _searchedIconInfoModel.value = _iconInfoModel.value
    }

    private suspend fun getIconRequestList(systemPackageList: List<IconInfo>) = withContext(Dispatchers.Default) {
        val lawniconsData = _iconInfoModel.value.iconInfo

        val systemData = systemPackageList.map { info ->
            info.getFirstLabelAndComponent()
        }

        val lawniconsComponents = lawniconsData
            .splitByComponentName()
            .map { it.getFirstLabelAndComponent().componentName }
            .sortedBy { it.lowercase() }
            .toSet()

        val commonItems = systemData.filter { it.componentName !in lawniconsComponents }
            .map {
                IconRequest(
                    label = it.label,
                    componentName = it.componentName,
                )
            }

        iconRequestList.value = IconRequestModel(
            list = commonItems,
            iconCount = commonItems.size,
        )
    }
}
