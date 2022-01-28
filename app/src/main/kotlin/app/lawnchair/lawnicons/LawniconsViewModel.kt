package app.lawnchair.lawnicons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LawniconsViewModel @Inject constructor(private val iconRepository: IconRepository) : ViewModel() {

    val iconInfoModel = iconRepository.iconInfoModel

    fun searchIcons(query: String) {
        viewModelScope.launch {
            iconRepository.search(query)
        }
    }
}
