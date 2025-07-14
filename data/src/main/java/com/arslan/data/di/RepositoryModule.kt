package com.arslan.data.di
import android.content.Context
import com.arslan.data.cleaner.ActivityProvider
import com.arslan.data.cleaner.CleanupManager
import com.arslan.data.cleaner.MediaDeletionHelper
import com.arslan.data.media.MediaManager
import com.arslan.data.repository.CleanupRepositoryImpl
import com.arslan.data.repository.MediaRepositoryImpl
import com.arslan.domain.repository.CleanupRepository
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


    @Provides
    @Singleton
    fun provideCleanupManager(
        @ApplicationContext context: Context,
        mediaManager: MediaManager,
        mediaDeletionHelper: MediaDeletionHelper
    ): CleanupManager {
        return CleanupManager(context, mediaManager,mediaDeletionHelper)
    }

    @Provides
    @Singleton
    fun provideMediaDeletionHelper(
        @ApplicationContext context: Context,
        activityProvider: ActivityProvider
    ): MediaDeletionHelper {
        return MediaDeletionHelper(context, activityProvider)
    }

    @Provides
    @Singleton
     fun bindCleanupRepository(
        cleanupManager: CleanupManager
    ): CleanupRepository {
        return CleanupRepositoryImpl(cleanupManager)
     }
}