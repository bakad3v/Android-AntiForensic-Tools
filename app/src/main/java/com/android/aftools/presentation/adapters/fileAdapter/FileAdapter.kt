package com.android.aftools.presentation.adapters.fileAdapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import coil.load
import com.android.aftools.R
import com.android.aftools.databinding.FileCardviewBinding
import com.android.aftools.domain.entities.FileDomain
import com.android.aftools.domain.entities.FileType

import javax.inject.Inject

/**
 * Recycler view adapter for usual files
 */
class FileAdapter @Inject constructor(
  diffCallback: MyFileAdapterDiffCallback,
) : ListAdapter<FileDomain, MyFileViewHolder>(diffCallback) {

  /**
   * Callbacks which can be set up from activity
   */
  var onMoreClickListener: ((FileDomain) -> Unit)? = null
  var onEditItemClickListener: ((FileDomain) -> Unit)? = null
  var onDeleteItemClickListener: ((Uri) -> Unit)? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFileViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = FileCardviewBinding.inflate(inflater, parent, false)
    return MyFileViewHolder(binding)
  }

  /**
   * Function for selecting image to display depending on the type of file
   */
  private fun ImageView.setImage(fyleType: FileType, uri: Uri) {
      when(fyleType) {
       FileType.DIRECTORY -> this.load(R.drawable.ic_baseline_folder_24_colored)
        FileType.IMAGE -> this.load(uri)
        FileType.USUAL_FILE -> this.load(R.drawable.ic_baseline_insert_drive_file_color)
      }
  }

  /**
   * Function for setting up text in recyclerview item
   */
  override fun onBindViewHolder(holder: MyFileViewHolder, position: Int) {
    val file = getItem(position)
    with(holder.binding) {
      imageView2.setImage(file.fileType, file.uri)
      path.text = file.name
      priority.text = root.context.getString(R.string.priority, file.priority)
      delete.setOnClickListener {
        onDeleteItemClickListener?.invoke(file.uri)
      }
      edit.setOnClickListener {
        onEditItemClickListener?.invoke(file)
      }
      more.setOnClickListener {
        onMoreClickListener?.invoke(file)
      }
    }
  }

}
