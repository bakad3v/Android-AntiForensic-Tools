package com.oasisfeng.island.presentation.adapters.fileAdapter

import androidx.recyclerview.widget.DiffUtil
import com.oasisfeng.island.domain.entities.FileDomain
import javax.inject.Inject

class MyFileAdapterDiffCallback @Inject constructor(): DiffUtil.ItemCallback<FileDomain>() {
  override fun areItemsTheSame(oldItem: FileDomain, newItem: FileDomain): Boolean =
    oldItem.name == newItem.name


  override fun areContentsTheSame(oldItem: FileDomain, newItem: FileDomain): Boolean =
    oldItem == newItem
}
