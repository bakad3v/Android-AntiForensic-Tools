package com.sonozaki.files.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.sonozaki.files.domain.entities.FileInfo
import javax.inject.Inject

class MyFileAdapterDiffCallback @Inject constructor(): DiffUtil.ItemCallback<FileInfo>() {
  override fun areItemsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean =
    oldItem.name == newItem.name


  override fun areContentsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean =
    oldItem == newItem
}
