package app.lawnchair.lawnicons.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.lawnicons.repository.IconRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LawniconsViewModel @Inject constructor(private val iconRepository: IconRepository) :
    ViewModel() {

    val iconInfoModel = iconRepository.iconInfoModel
    val searchedIconInfoModel = iconRepository.searchedIconInfoModel

    fun searchIcons(query: String) {
        viewModelScope.launch {
            iconRepository.search(query)
        }
    }
}
