package com.android.aftools.presentation.adapters.profileAdapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.android.aftools.R
import com.android.aftools.databinding.ProfileCardviewBinding
import com.android.aftools.domain.entities.ProfileDomain
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import javax.inject.Inject

/**
 * Recycler view adapter for profiles
 */
class ProfileAdapter @Inject constructor(
  diffCallback: MyProfileAdapterDiffCallback,
) : ListAdapter<ProfileDomain, MyProfileViewHolder>(diffCallback) {

  /**
   * Callbacks which can be set up from activity
   */
  var onDeleteItemClickListener: ((Int, Boolean) -> Unit)? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProfileViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = ProfileCardviewBinding.inflate(inflater, parent, false)
    return MyProfileViewHolder(binding)
  }

  /**
   * Function for selecting image to display depending on the status of profile
   */
  private fun MaterialButton.setStyle(delete: Boolean) {
      if (delete) {
        setText(R.string.not_delete)
        setIconResource(R.drawable.baseline_block_24)
        val color = MaterialColors.getColor(context, com.google.android.material.R.attr.colorTertiary, Color.GRAY)
        setTextColor(color)
        iconTint = ColorStateList.valueOf(color)
        strokeColor = ColorStateList.valueOf(color)
        return
      }
      setText(R.string.delete)
      setIconResource(R.drawable.ic_baseline_delete_24)
      val color = MaterialColors.getColor(context, com.google.android.material.R.attr.colorError, Color.GRAY)
      setTextColor(color)
      iconTint = ColorStateList.valueOf(color)
      strokeColor = ColorStateList.valueOf(color)
  }

  /**
   * Function for setting up text in recyclerview item
   */
  override fun onBindViewHolder(holder: MyProfileViewHolder, position: Int) {
    val profile = getItem(position)
    with(holder.binding) {
      name.text = profile.name
      id.text = holder.binding.root.context.getString(R.string.profile_id, profile.id)
      delete.setStyle(profile.toDelete)
      delete.setOnClickListener {
        onDeleteItemClickListener?.invoke(profile.id,!profile.toDelete)
      }
    }
  }

}
