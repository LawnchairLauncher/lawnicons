package app.lawnchair.lawnicons

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import dev.zacsweers.metro.createGraphFactory

class LawniconsApplication :
    Application(),
    ImageLoaderFactory {
    val lawniconsGraph by lazy {
        createGraphFactory<LawniconsGraph.Factory>().create(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }
}
