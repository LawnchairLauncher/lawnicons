package app.lawnchair.lawnicons

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LawniconsViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    val iconInfo = flow {
        val data = application.getIconInfo()
            .associateBy { it.name }.values
            .sortedBy { it.name.lowercase() }
        emit(data)
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null,
        )
}
