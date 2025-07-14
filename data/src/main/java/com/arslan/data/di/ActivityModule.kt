package com.arslan.data.di

import com.arslan.data.cleaner.ActivityProvider
import com.arslan.data.cleaner.ActivityProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ActivityModule {

    @Binds
    @Singleton
    abstract fun bindActivityProvider(
        activityProviderImpl: ActivityProviderImpl
    ): ActivityProvider
} 