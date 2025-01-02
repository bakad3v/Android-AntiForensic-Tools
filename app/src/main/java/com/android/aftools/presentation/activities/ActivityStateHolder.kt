package com.android.aftools.presentation.activities

import com.android.aftools.presentation.states.ActivityState

interface ActivityStateHolder {
    fun setActivityState(state: ActivityState)
}