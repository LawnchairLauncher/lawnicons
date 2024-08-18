package app.lawnchair.lawnicons.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.lawnicons.model.SearchMode
import app.lawnchair.lawnicons.repository.IconRepository
import app.lawnchair.lawnicons.repository.UserTipsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LawniconsViewModel @Inject constructor(
    private val iconRepository: IconRepository,
    private val userTipsRepository: UserTipsRepository,
) :
    ViewModel() {
    @JvmField
    val iconInfoModel = iconRepository.iconInfoModel

    @JvmField
    val searchedIconInfoModel = iconRepository.searchedIconInfoModel

    @JvmField
    val iconRequestModel = iconRepository.iconRequestList

    @JvmField
    val isIconRequestButtonClicked = userTipsRepository.hasClickedIconRequestButton()

    var expandSearch by mutableStateOf(false)

    private var _searchMode by mutableStateOf(SearchMode.LABEL)
    private var _searchTerm by mutableStateOf("")

    val searchMode: SearchMode
        get() = _searchMode

    val searchTerm: String
        get() = _searchTerm

    fun searchIcons(query: String) {
        _searchTerm = query
        viewModelScope.launch {
            iconRepository.search(searchMode, searchTerm)
        }
    }

    fun changeMode(mode: SearchMode) {
        _searchMode = mode
        viewModelScope.launch {
            iconRepository.search(searchMode, searchTerm)
        }
    }

    fun clearSearch() {
        _searchTerm = ""
        viewModelScope.launch {
            iconRepository.clearSearch()
        }
    }

    fun onIconRequestButtonClicked() {
        if (!userTipsRepository.hasClickedIconRequestButton()) {
            userTipsRepository.onIconRequestButtonClicked()
        }
    }
}
