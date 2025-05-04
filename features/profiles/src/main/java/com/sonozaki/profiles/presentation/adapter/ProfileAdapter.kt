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
    @Assisted(ON_STOP_ITEM_LISTENER) private val onStopItemClickListener: ((Int, Boolean) -> Unit)
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
      setText(com.sonozaki.resources.R.string.enable_deletion)
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
      id.text = root.context.getString(R.string.profile_id, profile.id)
      val current = if (profile.current) {
          root.context.getString(R.string.current)
      } else {
          null
      }
      val running = when (profile.running) {
          true -> root.context.getString(R.string.running)
          false -> root.context.getString(R.string.not_running)
          null -> root.context.getString(R.string.unknown)
      }
      status.text = if (current != null) {
          "$running, $current"
      } else {
          running
      }
      delete.setStyle(profile.toDelete)
      delete.setOnClickListener {
        onDeleteItemClickListener(profile.id,!profile.toDelete)
      }
      stop.visibility = booleanToVisibility(profile.running != false)
      stop.setOnClickListener {
          onStopItemClickListener(profile.id, profile.current)
      }
    }
  }

  @AssistedFactory
  interface Factory {
      fun create(
          @Assisted(ON_DELETE_ITEM_LISTENER) onDeleteItemClickListener: ((Int, Boolean) -> Unit),
          @Assisted(ON_STOP_ITEM_LISTENER) onStopItemClickListener: ((Int, Boolean) -> Unit)
      ): ProfileAdapter
  }

  companion object {
      private const val ON_DELETE_ITEM_LISTENER = "on_delete_item_listener"
      private const val ON_STOP_ITEM_LISTENER = "on_stop_item_listener"
  }

}
