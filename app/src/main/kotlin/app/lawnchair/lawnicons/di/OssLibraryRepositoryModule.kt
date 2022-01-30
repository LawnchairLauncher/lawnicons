package app.lawnchair.lawnicons.di

import android.app.Application
import app.lawnchair.lawnicons.repository.OssLibraryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OssLibraryRepositoryModule {

    @Provides
    @Singleton
    fun provideOssLibraryRepository(application: Application) = OssLibraryRepository(application = application)
}
