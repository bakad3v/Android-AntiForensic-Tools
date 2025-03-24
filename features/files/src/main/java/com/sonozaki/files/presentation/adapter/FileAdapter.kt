package com.sonozaki.files.presentation.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import coil.load
import com.sonozaki.entities.FileType
import com.sonozaki.files.R
import com.sonozaki.files.databinding.FileCardviewBinding
import com.sonozaki.files.domain.entities.FileInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Recycler view adapter for usual files
 */
class FileAdapter @AssistedInject constructor(
    diffCallback: MyFileAdapterDiffCallback,
    @Assisted(MORE_CLICK_LISTENER) private val onMoreClickListener: ((FileInfo) -> Unit),
    @Assisted(EDIT_ITEM_LISTENER) private val onEditItemClickListener: ((FileInfo) -> Unit),
    @Assisted private val onDeleteItemClickListener: ((Uri) -> Unit)
) : ListAdapter<FileInfo, MyFileViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFileViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FileCardviewBinding.inflate(inflater, parent, false)
        return MyFileViewHolder(binding)
    }

    /**
     * Function for selecting image to display depending on the type of file
     */
    private fun ImageView.setImage(fyleType: FileType, uri: Uri) {
        when (fyleType) {
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
                onDeleteItemClickListener(file.uri)
            }
            edit.setOnClickListener {
                onEditItemClickListener(file)
            }
            more.setOnClickListener {
                onMoreClickListener(file)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(MORE_CLICK_LISTENER) onMoreClickListener: ((FileInfo) -> Unit),
            @Assisted(EDIT_ITEM_LISTENER) onEditItemClickListener: ((FileInfo) -> Unit),
            onDeleteItemClickListener: ((Uri) -> Unit)
        ): FileAdapter
    }

    companion object {
        private const val MORE_CLICK_LISTENER = "more_click_listener"
        private const val EDIT_ITEM_LISTENER = "edit_item_listener"
    }

}
