package com.arslan.data.di
import android.content.Context
import com.arslan.data.media.MediaManager
import com.arslan.data.repository.MediaRepositoryImpl
import com.arslan.domain.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMediaManager(
        @ApplicationContext context: Context
    ): MediaManager {
        return MediaManager(context)
    }

    @Provides
    @Singleton
    fun provideMediaRepository(
        mediaManager: MediaManager
    ): MediaRepository {
        return MediaRepositoryImpl(mediaManager)
    }
}