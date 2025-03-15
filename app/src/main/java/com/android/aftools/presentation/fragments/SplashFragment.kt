package com.android.aftools.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.aftools.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.android.aftools.databinding.SplashScreenBinding
import com.android.aftools.presentation.viewmodels.SplashVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment: Fragment() {
    private val viewModel by viewModels<SplashVM>()
    private val controller by lazy { findNavController() }

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
        moveToNextScreen()
    }

    private fun moveToNextScreen() {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
            viewModel.passwordStatus.collect {
                val destination = if (it) {
                    SplashFragmentDirections.actionSplashFragmentToPassFragmentNav()
                } else {
                    SplashFragmentDirections.actionSplashFragmentToSetupPassFragment()
                }
                controller.navigate(destination)
            }
        }
    }
}