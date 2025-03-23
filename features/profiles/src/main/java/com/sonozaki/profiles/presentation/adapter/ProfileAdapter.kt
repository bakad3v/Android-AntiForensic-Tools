package com.sonozaki.profiles.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.sonozaki.entities.ProfileDomain
import com.sonozaki.profiles.R
import com.sonozaki.profiles.databinding.ProfileCardviewBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Recycler view adapter for profiles
 */
class ProfileAdapter @AssistedInject constructor(
    diffCallback: MyProfileAdapterDiffCallback,
    @Assisted private val onDeleteItemClickListener: ((Int, Boolean) -> Unit)
) : ListAdapter<ProfileDomain, MyProfileViewHolder>(diffCallback) {

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
      setText(com.sonozaki.resources.R.string.delete)
      setIconResource(com.sonozaki.resources.R.drawable.ic_baseline_delete_24)
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
        onDeleteItemClickListener(profile.id,!profile.toDelete)
      }
    }
  }

  @AssistedFactory
  interface Factory {
      fun create(onDeleteItemClickListener: ((Int, Boolean) -> Unit)): ProfileAdapter
  }

}
