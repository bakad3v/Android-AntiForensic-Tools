package com.oasisfeng.island.presentation.activities

import com.oasisfeng.island.presentation.states.ActivityState

interface ActivityStateHolder {
    fun setActivityState(state: ActivityState)
}