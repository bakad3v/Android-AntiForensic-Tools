package com.android.aftools.domain.repository

import com.sonozaki.entities.UISettings
import kotlinx.coroutines.flow.Flow

interface MainActivityRepository {
    val uiSettings: Flow<UISettings>
}