package app.lawnchair.lawnicons.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.lawnicons.model.SearchMode
import app.lawnchair.lawnicons.repository.IconRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LawniconsViewModel @Inject constructor(private val iconRepository: IconRepository) :
    ViewModel() {

    val iconInfoModel = iconRepository.iconInfoModel
    val searchedIconInfoModel = iconRepository.searchedIconInfoModel
    var searchMode by mutableStateOf(SearchMode.NAME)
        private set

    fun searchIcons(query: String) {
        viewModelScope.launch {
            iconRepository.search(searchMode, query)
        }
    }

    fun changeMode(mode: SearchMode) {
        this.searchMode = mode
    }
}
