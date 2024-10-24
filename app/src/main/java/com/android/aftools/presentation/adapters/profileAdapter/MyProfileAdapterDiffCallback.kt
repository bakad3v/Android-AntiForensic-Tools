package com.android.aftools.presentation.adapters.profileAdapter

import androidx.recyclerview.widget.DiffUtil
import com.android.aftools.domain.entities.ProfileDomain
import javax.inject.Inject

class MyProfileAdapterDiffCallback @Inject constructor(): DiffUtil.ItemCallback<ProfileDomain>() {
  override fun areItemsTheSame(oldItem: ProfileDomain, newItem: ProfileDomain): Boolean =
    oldItem.id == newItem.id


  override fun areContentsTheSame(oldItem: ProfileDomain, newItem: ProfileDomain): Boolean =
    oldItem == newItem
}
