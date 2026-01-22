package app.lawnchair.lawnicons

import android.app.Application
import dev.zacsweers.metro.createGraphFactory

class LawniconsApplication : Application() {
    val lawniconsGraph by lazy {
        createGraphFactory<LawniconsGraph.Factory>().create(this)
    }
}
