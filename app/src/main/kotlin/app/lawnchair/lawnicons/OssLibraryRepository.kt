package app.lawnchair.lawnicons

import android.app.Application
import app.lawnchair.ossnotices.OssLibrary
import app.lawnchair.ossnotices.getOssLibraries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class OssLibraryRepository @Inject constructor(application: Application) {

    val ossLibraries = MutableStateFlow<List<OssLibrary>?>(value = null)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        coroutineScope.launch {
            ossLibraries.value = application.getOssLibraries(
                thirdPartyLicenseMetadataId = R.raw.third_party_license_metadata,
            )
        }
    }
}
