package com.sonozaki.settings.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.settings.databinding.AboutSettingsFragmentBinding

class AboutSettingsFragment: Fragment() {

    private val aboutBinding: AboutSettingsFragmentBinding get() = _aboutBinding?: throw RuntimeException("AboutSettingsFragwentBinding == null")
    private var _aboutBinding: AboutSettingsFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _aboutBinding =
            AboutSettingsFragmentBinding.inflate(inflater,container,false)
        return aboutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMainActivityState()
        aboutBinding.close.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        setupWebview()
    }

    private fun setupWebview() {
        aboutBinding.helpWebview.apply {
            settings.javaScriptEnabled = true
            loadUrl(WIKI_LINK)
        }
    }

    private fun setMainActivityState() {
        val activity = requireActivity()
        if (activity is ActivityStateHolder) {
            activity.setActivityState(ActivityState.NoActionBarActivityState)
        }
    }

    companion object {
        private const val WIKI_LINK = "https://github.com/bakad3v/Android-AntiForensic-Tools/wiki/Android-AntiForensic-Tools-wiki"
    }
}