package com.android.aftools.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class ServicesModule {
  @Provides
  fun provideCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.Main)
}
