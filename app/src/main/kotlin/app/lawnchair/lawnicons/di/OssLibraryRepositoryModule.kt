package app.lawnchair.lawnicons.di

import android.app.Application
import app.lawnchair.lawnicons.repository.OssLibraryRepository
import app.lawnchair.lawnicons.repository.OssLibraryRepositoryImpl
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
    fun provideOssLibraryRepository(application: Application): OssLibraryRepository = OssLibraryRepositoryImpl(application = application)
}
