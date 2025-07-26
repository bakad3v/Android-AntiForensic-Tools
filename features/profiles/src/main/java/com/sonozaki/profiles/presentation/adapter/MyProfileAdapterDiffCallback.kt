package com.sonozaki.profiles.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.sonozaki.profiles.entities.ProfileUI
import javax.inject.Inject

class MyProfileAdapterDiffCallback @Inject constructor(): DiffUtil.ItemCallback<ProfileUI>() {
  override fun areItemsTheSame(oldItem: ProfileUI, newItem: ProfileUI): Boolean =
    oldItem.profileDomain.id == newItem.profileDomain.id


  override fun areContentsTheSame(oldItem: ProfileUI, newItem: ProfileUI): Boolean =
    oldItem == newItem
}
