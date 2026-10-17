package com.android.aftools

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.android.aftools.domain.usecases.CheckTestOnlyStatus
import com.sonozaki.resources.IO_DISPATCHER
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class AndroidAntiForensicTools: Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var checkTestOnlyStatus: CheckTestOnlyStatus

    @Inject
    @Named(IO_DISPATCHER)
    lateinit var dispatcher: CoroutineDispatcher

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(dispatcher).launch {
            checkTestOnlyStatus()
        }
    }


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
}
