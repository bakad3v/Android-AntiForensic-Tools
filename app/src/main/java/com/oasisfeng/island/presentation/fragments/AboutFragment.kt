package com.oasisfeng.island.presentation.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.oasisfeng.island.R
import com.oasisfeng.island.databinding.AboutFragmentBinding
import com.oasisfeng.island.presentation.activities.MainActivity
import com.oasisfeng.island.presentation.states.ActivityState

/**
 * Fragment for "About app" screen
 */
class AboutFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val aboutBinding =
      AboutFragmentBinding.inflate(inflater,container,false)
    aboutBinding.aboutmessage.movementMethod = LinkMovementMethod.getInstance() //enabling links
    aboutBinding.aboutmessage.text = HtmlCompat.fromHtml(getString(R.string.about_long), HtmlCompat.FROM_HTML_MODE_LEGACY)
    return aboutBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setMainActivityState()
  }

  private fun setMainActivityState() {
    (activity as MainActivity).setActivityState(ActivityState.NormalActivityState(getString(R.string.about)))
  }

}
