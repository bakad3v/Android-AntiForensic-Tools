package com.sonozaki.profiles.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.sonozaki.profiles.R
import com.sonozaki.profiles.databinding.ProfileCardviewBinding
import com.sonozaki.profiles.entities.ProfileUI
import com.sonozaki.utils.booleanToVisibility
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Recycler view adapter for profiles
 */
class ProfileAdapter @AssistedInject constructor(
    diffCallback: MyProfileAdapterDiffCallback,
    @Assisted(ON_DELETE_ITEM_LISTENER) private val onDeleteItemClickListener: ((Int, Boolean) -> Unit),
    @Assisted(ON_STOP_ITEM_LISTENER) private val onStopItemClickListener: ((Int, Boolean) -> Unit),
    @Assisted private val onItemDeletionForbidden: () -> Unit
) : ListAdapter<ProfileUI, MyProfileViewHolder>(diffCallback) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProfileViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = ProfileCardviewBinding.inflate(inflater, parent, false)
    return MyProfileViewHolder(binding)
  }

  /**
   * Function for selecting image to display depending on the status of profile
   */
  private fun MaterialButton.setStyle(delete: Boolean, isDeletionPermitted: Boolean) {
      if (delete) {
        setText(R.string.not_delete)
        setIconResource(R.drawable.baseline_block_24)
        val color = if (isDeletionPermitted) {
            MaterialColors.getColor(
                context,
                com.google.android.material.R.attr.colorTertiary,
                Color.GRAY
            )
        } else {
            Color.GRAY
        }
        setTextColor(color)
        iconTint = ColorStateList.valueOf(color)
        strokeColor = ColorStateList.valueOf(color)
        return
      }
      setText(com.sonozaki.resources.R.string.enable_deletion)
      setIconResource(com.sonozaki.resources.R.drawable.ic_baseline_delete_24)
      val color = if (isDeletionPermitted) {
          MaterialColors.getColor(
              context,
              com.google.android.material.R.attr.colorError,
              Color.GRAY
          )
      } else {
          Color.GRAY
      }
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
      name.text = profile.profileDomain.name
      id.text = root.context.getString(R.string.profile_id, profile.profileDomain.id)
      val current = if (profile.profileDomain.current) {
          root.context.getString(R.string.current)
      } else {
          null
      }
      val running = when (profile.profileDomain.running) {
          true -> root.context.getString(R.string.running)
          false -> root.context.getString(R.string.not_running)
          null -> root.context.getString(R.string.unknown)
      }
      status.text = if (current != null) {
          "$running, $current"
      } else {
          running
      }
      delete.setStyle(profile.profileDomain.toDelete, profile.isDeletionPermitted)
      if (profile.isDeletionPermitted) {
          delete.setOnClickListener {
              onDeleteItemClickListener(profile.profileDomain.id, !profile.profileDomain.toDelete)
          }
      } else {
          delete.setOnClickListener { onItemDeletionForbidden() }
      }
      stop.visibility = booleanToVisibility(profile.profileDomain.running != false)
      stop.setOnClickListener {
          onStopItemClickListener(profile.profileDomain.id, profile.profileDomain.current)
      }
    }
  }

  @AssistedFactory
  interface Factory {
      fun create(
          @Assisted(ON_DELETE_ITEM_LISTENER) onDeleteItemClickListener: ((Int, Boolean) -> Unit),
          @Assisted(ON_STOP_ITEM_LISTENER) onStopItemClickListener: ((Int, Boolean) -> Unit),
          @Assisted onItemDeletionForbidden: () -> Unit
      ): ProfileAdapter
  }

  companion object {
      private const val ON_DELETE_ITEM_LISTENER = "on_delete_item_listener"
      private const val ON_STOP_ITEM_LISTENER = "on_stop_item_listener"
  }

}
