package com.sonozaki.settings.presentation.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.sonozaki.activitystate.ActivityState
import com.sonozaki.activitystate.ActivityStateHolder
import com.sonozaki.settings.R
import com.sonozaki.settings.databinding.AboutSettingsFragmentBinding

class AboutSettingsFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val aboutBinding =
            AboutSettingsFragmentBinding.inflate(inflater,container,false)
        aboutBinding.aboutmessage.movementMethod = LinkMovementMethod.getInstance() //enabling links
        aboutBinding.aboutmessage.text = HtmlCompat.fromHtml(getString(R.string.settings_faq), HtmlCompat.FROM_HTML_MODE_LEGACY)
        aboutBinding.close.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return aboutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMainActivityState()
    }

    private fun setMainActivityState() {
        val activity = requireActivity()
        if (activity is ActivityStateHolder) {
            activity.setActivityState(ActivityState.NoActionBarActivityState)
        }
    }
}