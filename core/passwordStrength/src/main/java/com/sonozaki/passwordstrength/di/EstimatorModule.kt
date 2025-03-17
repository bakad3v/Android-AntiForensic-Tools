package com.sonozaki.passwordstrength.di

import com.nulabinc.zxcvbn.Zxcvbn
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EstimatorModule {
    @Provides
    @Singleton
    fun provideZxcvbn(): Zxcvbn = Zxcvbn()
}