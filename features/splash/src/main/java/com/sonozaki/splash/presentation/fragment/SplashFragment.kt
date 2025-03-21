package com.sonozaki.splash.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.splash.databinding.SplashScreenBinding
import com.sonozaki.splash.domain.router.SplashRouter
import com.sonozaki.splash.presentation.viewmodel.SplashVM
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment: Fragment() {
    private val viewModel by viewModels<SplashVM>()
    private val controller by lazy { findNavController() }

    @Inject
    lateinit var router: SplashRouter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding =
            SplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivity()
        moveToNextScreen()
    }

    private fun moveToNextScreen() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.passwordStatus.collect {
                if (it) {
                    router.enterPassword(controller)
                } else {
                   router.createPassword(controller)
                }
            }
        }
    }

    private fun setupActivity() {
        val activity = requireActivity()
        if (activity is ActivityStateHolder) {
            activity.setActivityState(
                ActivityState.NoActionBarNoDrawerActivityState
            )
        }
    }
}