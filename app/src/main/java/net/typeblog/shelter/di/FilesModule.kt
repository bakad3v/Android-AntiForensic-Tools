package net.typeblog.shelter.di

import net.typeblog.shelter.domain.entities.FilesSortOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FilesModule {

    @Provides
    @Singleton
    fun provideFilesSortOrderFlow(): MutableStateFlow<FilesSortOrder> =
        MutableStateFlow(FilesSortOrder.NAME_ASC)

}
