package app.lawnchair.lawnicons

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.ossnotices.getOssLibraries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AcknowledgementsViewModel @Inject constructor(application: Application) : ViewModel() {

    val ossLibraries = flow {
        val ossLibraries = application.getOssLibraries(thirdPartyLicenseMetadataId = R.raw.third_party_license_metadata)
        emit(ossLibraries)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
}
