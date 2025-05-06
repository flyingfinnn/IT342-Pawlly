package com.sysinteg.pawlly.di

import com.sysinteg.pawlly.userApi
import com.sysinteg.pawlly.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideUserApi(): UserApi = userApi
} 