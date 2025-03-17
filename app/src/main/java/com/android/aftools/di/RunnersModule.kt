package com.android.aftools.di

import com.sonozaki.resources.AFU_RUNNER
import com.sonozaki.resources.BFU_RUNNER
import com.sonozaki.services.services.AFUActivitiesRunner
import com.sonozaki.services.services.ActivityRunner
import com.sonozaki.services.services.BFUActivitiesRunner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RunnersModule {
    @Binds
    @Named(AFU_RUNNER)
    @Singleton
    abstract fun bindAFURunner(afuActivitiesRunner: AFUActivitiesRunner): ActivityRunner

    @Binds
    @Named(BFU_RUNNER)
    @Singleton
    abstract fun bindBFURunner(afuActivitiesRunner: BFUActivitiesRunner): ActivityRunner

}