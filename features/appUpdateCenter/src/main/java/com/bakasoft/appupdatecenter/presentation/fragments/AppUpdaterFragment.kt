package com.bakasoft.appupdatecenter.presentation.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bakasoft.appupdatecenter.R
import com.bakasoft.appupdatecenter.databinding.UpdateCenterFragmentBinding
import com.bakasoft.appupdatecenter.presentation.actions.AppUpdaterActions
import com.bakasoft.appupdatecenter.presentation.state.AppUpdaterState
import com.bakasoft.appupdatecenter.presentation.state.SelectedOption
import com.bakasoft.appupdatecenter.presentation.viewmodel.AppUpdaterViewModel
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.sonozaki.utils.UIText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppUpdaterFragment: Fragment() {
    private val viewModel by viewModels<AppUpdaterViewModel>()
    private var _binding: UpdateCenterFragmentBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("UpdateCenterFragmentBinding == null")

    private val permissionRequestLauncher = registerForActivityResult(RequestPermission()
    ) { isGranted: Boolean ->
        viewModel.installUpdate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            UpdateCenterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivity()
        observeState()
        observeActions()
        setupClickable()
    }

    private fun observeActions() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.actionsFlow.collect {
                when(it) {
                    AppUpdaterActions.START_UPDATE -> {
                        when {
                            requireContext().checkSelfPermission(
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                viewModel.installUpdate()
                            }
                            else -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionRequestLauncher.launch(
                                        Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.installUpdate()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupActivity() {
        val activity = requireActivity()
        if (activity is ActivityStateHolder)
            activity.setActivityState(
                ActivityState.NormalActivityState(
                    getString(
                        R.string.app_update_center
                    )
                )
            )
    }

    private fun showLoading() {
        with(binding) {
            loading.isVisible = true
            scrollUpdateCenter.isVisible = false
            errorLayout.isVisible = false
        }
    }


    private fun showError(error: UIText.StringResource) {
        with(binding) {
            loading.isVisible = false
            scrollUpdateCenter.isVisible = false
            errorLayout.isVisible = true
            errorText.text = error.asString(requireContext())
            reloadButton.setOnClickListener {
                viewModel.checkUpdates()
            }
        }
    }

    private fun setupClickable() {
        with(binding) {
            rbRegularVersion.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    viewModel.setSelectedOption(SelectedOption.INSTALL_USUAL)
                }
            }
            rbRootVersion.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    viewModel.setSelectedOption(SelectedOption.INSTALL_TESTONLY)
                }
            }
            reloadButton.setOnClickListener {
                viewModel.checkUpdates()
            }
            chkEnableNotifications.setOnCheckedChangeListener { _, checked ->
                viewModel.setUpdatePopupStatus(checked)
            }
        }
    }

    private fun showData(data: AppUpdaterState.Data) {
        with(binding) {
            loading.isVisible = false
            scrollUpdateCenter.isVisible = true
            errorLayout.isVisible = false
            tvLatestVersionHeader.text = requireContext().getString(R.string.latest_version, data.appLatestVersion.version)
            tvUpdateStatusSubtitle.text = if (data.appLatestVersion.newVersion) {
                requireContext().getString(R.string.update_app)
            } else if (!data.appLatestVersion.isTestOnly) {
                requireContext().getString(R.string.install_testonly)
            } else {
                requireContext().getString(R.string.up_to_date)
            }
            rbRegularVersion.isEnabled = data.activeUsualVersion
            rbRootVersion.isEnabled = data.activeTestOnlyVersion
            when(data.selectedOption) {
                SelectedOption.NONE -> {
                    rbRegularVersion.isChecked = false
                    rbRootVersion.isChecked = false
                }
                SelectedOption.INSTALL_USUAL -> {
                    rbRegularVersion.isChecked = true
                    rbRootVersion.isChecked = false
                }
                SelectedOption.INSTALL_TESTONLY -> {
                    rbRegularVersion.isChecked = false
                    rbRootVersion.isChecked = true
                }
            }
            chkEnableNotifications.isChecked = data.showUpdatePopup
            btnInstallUpdate.isEnabled = when (data.selectedOption) {
                SelectedOption.NONE -> false

                SelectedOption.INSTALL_USUAL ->
                    data.activeUsualVersion

                SelectedOption.INSTALL_TESTONLY ->
                    data.activeTestOnlyVersion
            }
            val path = when(data.selectedOption) {
                SelectedOption.NONE -> ""

                SelectedOption.INSTALL_USUAL ->
                    data.appLatestVersion.usualVersionLink

                SelectedOption.INSTALL_TESTONLY ->
                    data.appLatestVersion.adbVersionLink
            }
            btnInstallUpdate.setOnClickListener {
                viewModel.setInstallerData(path, data.selectedOption == SelectedOption.INSTALL_TESTONLY)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.appUpdateCenterState.collect {
                when(it) {
                    is AppUpdaterState.Loading -> {
                        showLoading()
                    }
                    is AppUpdaterState.Error -> {
                        showError(it.error)
                    }
                    is AppUpdaterState.Data -> {
                        showData(it)
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
