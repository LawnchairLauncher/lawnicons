package app.lawnchair.lawnicons.api

import app.lawnchair.lawnicons.util.kotlinxJson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
class GithubApiModule {

    @Provides
    @Singleton
    fun providesGitHubContributorsApi(): GitHubContributorsAPI {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(kotlinxJson.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }
}
