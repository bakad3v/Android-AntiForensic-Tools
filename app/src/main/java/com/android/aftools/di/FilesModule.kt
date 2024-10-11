package com.android.aftools.di

import com.android.aftools.data.repositories.FilesRepositoryImpl
import com.android.aftools.domain.entities.FilesSortOrder
import com.android.aftools.domain.repositories.FilesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FilesModule {
  @Binds
  @Singleton
  abstract fun bindFilesRepository(filesRepositoryImpl: FilesRepositoryImpl): FilesRepository

  companion object {

    @Provides
    @Singleton
    fun provideFilesSortOrderFlow(): MutableStateFlow<FilesSortOrder> =
      MutableStateFlow(FilesSortOrder.NAME_ASC)
  }
}
