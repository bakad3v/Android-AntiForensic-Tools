package net.typeblog.shelter.presentation.activities

import net.typeblog.shelter.presentation.states.ActivityState

interface ActivityStateHolder {
    fun setActivityState(state: ActivityState)
}