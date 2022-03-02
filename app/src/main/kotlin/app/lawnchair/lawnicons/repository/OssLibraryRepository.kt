package app.lawnchair.lawnicons.repository

import android.app.Application
import androidx.compose.ui.text.AnnotatedString
import app.lawnchair.lawnicons.R
import app.lawnchair.ossnotices.OssLibrary
import app.lawnchair.ossnotices.getOssLibraries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class OssLibraryRepository @Inject constructor(private val application: Application) {

    val ossLibraries = MutableStateFlow<List<OssLibrary>?>(value = null)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        coroutineScope.launch {
            ossLibraries.value = application.getOssLibraries(
                thirdPartyLicenseMetadataId = R.raw.third_party_license_metadata,
            )
        }
    }

    fun getNoticeForOssLibrary(
        ossLibraryName: String,
        annotate: (String) -> AnnotatedString,
    ) = ossLibraries.filterNotNull().map {
        it.find { it.name == ossLibraryName }?.run {
            val notice = getNotice(application, R.raw.third_party_licenses)
            annotate(notice)
        }
    }
}
