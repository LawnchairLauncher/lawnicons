package app.lawnchair.lawnicons.di

import android.app.Application
import android.content.Context
import app.lawnchair.lawnicons.repository.UserTipsRepository
import app.lawnchair.lawnicons.repository.UserTipsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserTipsRepositoryModule {

    @Provides
    @Singleton
    fun provideIconRepository(application: Application): UserTipsRepository = UserTipsRepositoryImpl(
        application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE),
    )
}
