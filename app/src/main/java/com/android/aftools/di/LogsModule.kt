package com.android.aftools.di

import com.android.aftools.TopLevelFunctions.getEpochDays
import com.android.aftools.data.repositories.LogsRepositoryImpl
import com.android.aftools.domain.repositories.LogsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LogsModule {
  @Binds
  @Singleton
  abstract fun bindLogsRepository(filesRepositoryImpl: LogsRepositoryImpl): LogsRepository

  companion object {

    @Provides
    @Singleton
    fun provideDaysFlow(): MutableStateFlow<Long> {
      return MutableStateFlow(LocalDateTime.now().getEpochDays())
    }


  }
}
