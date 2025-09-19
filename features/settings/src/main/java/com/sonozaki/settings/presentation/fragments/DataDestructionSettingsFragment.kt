package com.sonozaki.settings.presentation.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.DataDestructionSettingsFragmentBinding
import com.sonozaki.settings.domain.routers.SettingsRouter
import com.sonozaki.settings.presentation.viewmodel.DataDestructionSettingsVM
import com.sonozaki.settings.presentation.viewmodel.DataDestructionSettingsVM.Companion.CLEAR_DATA_DIALOG
import com.sonozaki.settings.presentation.viewmodel.DataDestructionSettingsVM.Companion.CLEAR_DIALOG
import com.sonozaki.settings.presentation.viewmodel.DataDestructionSettingsVM.Companion.DESTROY_DATA_DIALOG
import com.sonozaki.settings.presentation.viewmodel.DataDestructionSettingsVM.Companion.HIDE_DIALOG
import com.sonozaki.settings.presentation.viewmodel.DataDestructionSettingsVM.Companion.SELF_DESTRUCTION_DIALOG
import com.sonozaki.settings.presentation.viewmodel.DataDestructionSettingsVM.Companion.TRIM_DIALOG
import com.sonozaki.settings.presentation.viewmodel.DataDestructionSettingsVM.Companion.WIPE_DIALOG
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DataDestructionSettingsFragment: AbstractSettingsFragment() {
    override val viewModel: DataDestructionSettingsVM by viewModels()
    private var _binding: DataDestructionSettingsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("DataDestructionSettingsFragmentBinding == null")

    @Inject
    lateinit var router: SettingsRouter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataDestructionSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivity(R.string.data_destruction_settings)
        listenDialogResults()
        setupButtonsAndSwitches()
    }

    private fun setupButtonsAndSwitches() {
        listenSettings()
        checkPermissions()
        setupClickableElements()
    }

    private val switchWipeListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setWipe(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showWipeDialog()
    }

    private val switchTrimListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setRunTRIM(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showTRIMDialog()
    }

    private val switchSelfDestructListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setRemoveItself(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showSelfDestructionDialog()
    }

    private val switchHideListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setHide(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showHideDialog()
    }

    private val switchClearListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setClear(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showClearDialog()
    }

    private val switchClearDataListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        if (!checked) {
            viewModel.setClearData(false)
            return@OnCheckedChangeListener
        }
        switch.isChecked = false
        viewModel.showClearDataDialog()
    }

    private fun setupClickableElements() {
        with(binding) {
            destroyData.setOnClickListener {
                viewModel.destroyDataDialog()
            }
        }
    }

    private fun checkPermissions() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.permissionsState.collect {
                val rootOrDhizuku = it.isRoot || it.isOwner
                val rootOrShizuku = it.isRoot || it.isShizuku
                with(binding) {
                    runTrimItem.setSwitchEnabled(rootOrShizuku)
                    wipeItem.setSwitchEnabled(
                        rootOrDhizuku || it.isAdmin && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                    )
                    removeItselfItem.setSwitchEnabled(rootOrShizuku)
                    hideAppItem.setSwitchEnabled(
                        rootOrDhizuku && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    )
                    clearItselfItem.setSwitchEnabled(rootOrDhizuku)
                }
            }
        }
    }


    private fun listenSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.settingsState.collect {
                with(binding) {
                    wipeItem.setCheckedProgrammatically(it.wipe, switchWipeListener)
                    runTrimItem.setCheckedProgrammatically(it.trim, switchTrimListener)
                    removeItselfItem.setCheckedProgrammatically(
                        it.removeItself,
                        switchSelfDestructListener
                    )
                    hideAppItem.setCheckedProgrammatically(it.hideItself, switchHideListener)
                    clearItselfItem.setCheckedProgrammatically(it.clearItself, switchClearListener)
                    clearDataItem.setCheckedProgrammatically(it.clearData, switchClearDataListener)
                }
            }
        }
    }

    private fun listenDialogResults() {
        listenQuestionDialog(TRIM_DIALOG) {
            viewModel.setRunTRIM(true)
        }
        listenQuestionDialog(WIPE_DIALOG) {
            viewModel.setWipe(true)
        }
        listenQuestionDialog(
            SELF_DESTRUCTION_DIALOG,
        ) {
            viewModel.setRemoveItself(true)
        }
        listenQuestionDialog(
            HIDE_DIALOG,
        ) {
            viewModel.setHide(true)
        }
        listenQuestionDialog(
            CLEAR_DIALOG
        ) {
            viewModel.setClear(true)
        }
        listenQuestionDialog(
            CLEAR_DATA_DIALOG
        ) {
            viewModel.setClearData(true)
        }
        listenQuestionDialog(DESTROY_DATA_DIALOG
        ) {
            router.startService(requireContext())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}