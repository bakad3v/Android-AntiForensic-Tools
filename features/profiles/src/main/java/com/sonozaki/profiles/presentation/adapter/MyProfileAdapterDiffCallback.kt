package com.sonozaki.profiles.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.sonozaki.entities.ProfileDomain
import javax.inject.Inject

class MyProfileAdapterDiffCallback @Inject constructor(): DiffUtil.ItemCallback<ProfileDomain>() {
  override fun areItemsTheSame(oldItem: ProfileDomain, newItem: ProfileDomain): Boolean =
    oldItem.id == newItem.id


  override fun areContentsTheSame(oldItem: ProfileDomain, newItem: ProfileDomain): Boolean =
    oldItem == newItem
}
