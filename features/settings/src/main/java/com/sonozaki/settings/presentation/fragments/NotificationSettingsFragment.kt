package com.sonozaki.settings.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import com.sonozaki.entities.NotificationSettings
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.NotificationsSettingsFragmentBinding
import com.sonozaki.settings.presentation.viewmodel.NotificationSettingsVM
import com.sonozaki.settings.presentation.viewmodel.NotificationSettingsVM.Companion.NOTIFICATION_SETTINGS_DIALOG
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationSettingsFragment: AbstractSettingsFragment() {
    override val viewModel: NotificationSettingsVM by viewModels()

    private var _binding: NotificationsSettingsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("NotificationSettingsFragmentBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            NotificationsSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivity(R.string.notification_hiding)
        setupPermissions()
        listenNotificationSettings()
        listenDialogs()
    }

    private val switchNotificationListener = CompoundButton.OnCheckedChangeListener { switch, checked ->
        viewModel.showNotificationSettingsDialog()
    }

    private fun setupPermissions() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.permissionsState.collect {
                with(binding) {
                    notificationItem.setSwitchEnabled(it.isRoot)
                }
            }
        }
    }



    private fun listenNotificationSettings() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.notificationSettingsState.collect {
                with(binding) {
                    notificationItem.setCheckedProgrammatically(
                        it == NotificationSettings.ENABLED,
                        switchNotificationListener
                    )
                }
            }
        }
    }

    private fun listenDialogs() {
        listenQuestionDialog(
            NOTIFICATION_SETTINGS_DIALOG,
        ) {
            setNotificationSettings()
        }
    }

    private fun setNotificationSettings() {
        startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}