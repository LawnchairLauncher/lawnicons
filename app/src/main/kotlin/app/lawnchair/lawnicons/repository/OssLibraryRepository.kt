package app.lawnchair.lawnicons.repository

import android.app.Application
import androidx.compose.ui.text.AnnotatedString
import app.lawnchair.lawnicons.model.OssLibrary
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


class OssLibraryRepository @Inject constructor(private val application: Application) {

    val ossLibraries: StateFlow<List<OssLibrary>> = flow {
        val jsonString = application.resources.assets.open("artifacts.json")
            .bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<OssLibrary>>() {}.type
        val ossLibraries = Gson().fromJson<List<OssLibrary>>(jsonString, listType)
            .asSequence()
            .distinctBy { "${it.groupId}:${it.artifactId}" }
            .sortedBy { it.name }
            .toList()
        emit(ossLibraries)
    }
        .flowOn(Dispatchers.IO)
        .stateIn(MainScope(), SharingStarted.Lazily, emptyList())

    fun getNoticeForOssLibrary(
        ossLibraryName: String,
        annotate: (String) -> AnnotatedString,
    ) = ossLibraries.map { it ->
        it.find { it.name == ossLibraryName }?.run {
            val string = (spdxLicenses ?: unknownLicenses)?.firstOrNull()?.url.orEmpty()
            annotate(string)
        }
    }
}
