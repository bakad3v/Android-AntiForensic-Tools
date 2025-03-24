package com.android.aftools.di

import com.sonozaki.utils.TopLevelFunctions.getEpochDays
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LogsModule {
    @Provides
    @Singleton
    fun provideDaysFlow(): MutableStateFlow<Long> {
      return MutableStateFlow(LocalDateTime.now().getEpochDays())
    }
}
