package com.sonozaki.settings.presentation.fragments

import androidx.fragment.app.Fragment
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.dialogs.DialogLauncher
import com.sonozaki.dialogs.QuestionDialog
import com.sonozaki.settings.presentation.viewmodel.AbstractSettingsVM
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine


abstract class AbstractSettingsFragment: Fragment() {
    abstract val viewModel: AbstractSettingsVM
    private val dialogLauncher by lazy {
        DialogLauncher(
            parentFragmentManager,
            context
        )
    }

    protected fun setupActivity(stringId: Int) {
        val activity = requireActivity()
        if (activity is ActivityStateHolder)
            activity.setActivityState(
                ActivityState.NormalActivityState(
                    getString(stringId)
                )
            )
    }

    /**
     * Setting up dialog launcher
     */
    protected fun setupDialogs() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.settingsActionsFlow.collect {
                dialogLauncher.launchDialogFromAction(it)
            }
        }
    }

    protected fun listenQuestionDialog(tag: String, function: () -> Unit) {
        QuestionDialog.setupListener(
            parentFragmentManager,
            tag,
            viewLifecycleOwner
        ) {
            function()
        }
    }
}