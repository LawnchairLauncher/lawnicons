package app.lawnchair.lawnicons.data.repository

import android.app.Application
import app.lawnchair.lawnicons.LawniconsScope
import app.lawnchair.lawnicons.data.kotlinxJson
import app.lawnchair.lawnicons.data.model.OssLibrary
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

interface OssLibraryRepository {
    val ossLibraries: StateFlow<List<OssLibrary>>
}

@SingleIn(LawniconsScope::class)
@ContributesBinding(LawniconsScope::class)
@Inject
class OssLibraryRepositoryImpl constructor(private val application: Application) :
    OssLibraryRepository {

    private val coroutineScope = MainScope()

    override val ossLibraries: StateFlow<List<OssLibrary>> = flow {
        val jsonString = application.resources.assets.open("licenses.json")
            .bufferedReader().use { it.readText() }
        val ossLibraries = kotlinxJson.decodeFromString<List<OssLibrary>>(jsonString)
            .asSequence()
            .distinctBy { "${it.groupId}:${it.artifactId}" }
            .distinctBy { "${it.groupId}:${it.name}" } // Handle cases with same name but different artifactId.
            .sortedBy { it.name }
            .toList()
        emit(ossLibraries)
    }
        .flowOn(Dispatchers.IO)
        .stateIn(coroutineScope, SharingStarted.Lazily, emptyList())
}
