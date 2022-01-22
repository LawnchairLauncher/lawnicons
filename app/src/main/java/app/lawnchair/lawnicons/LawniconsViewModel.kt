package app.lawnchair.lawnicons

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LawniconsViewModel @Inject constructor(private val iconRepository: IconRepository) : ViewModel() {

    val iconInfoModel = iconRepository.iconInfoModel

    fun searchIcons(query: String) {
        iconRepository.search(query)
    }
}
