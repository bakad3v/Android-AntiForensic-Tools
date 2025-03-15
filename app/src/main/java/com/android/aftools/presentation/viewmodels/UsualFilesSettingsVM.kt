package com.android.aftools.presentation.viewmodels

import com.android.aftools.domain.usecases.filesDatabase.ClearDbUseCase
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.aftools.R
import com.android.aftools.domain.entities.FileDomain
import com.android.aftools.domain.entities.FilesSortOrder
import com.android.aftools.domain.usecases.filesDatabase.ChangeFilePriorityUseCase
import com.android.aftools.domain.usecases.filesDatabase.ChangeSortOrderUseCase
import com.android.aftools.domain.usecases.filesDatabase.DeleteMyFileUseCase
import com.android.aftools.domain.usecases.filesDatabase.GetFilesDbUseCase
import com.android.aftools.domain.usecases.filesDatabase.GetSortOrderUseCase
import com.android.aftools.domain.usecases.filesDatabase.InsertMyFileUseCase
import com.android.aftools.domain.usecases.settings.GetSettingsUseCase
import com.android.aftools.domain.usecases.settings.SetDeleteFilesUseCase
import com.android.aftools.presentation.states.DeletionDataState
import com.android.aftools.presentation.utils.UIText
import com.android.aftools.presentation.actions.FileSettingsAction
import com.android.aftools.presentation.dialogs.DialogActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsualFilesSettingsVM @Inject constructor(
  getFilesDbUseCase: GetFilesDbUseCase,
  private val deleteMyFileUseCase: DeleteMyFileUseCase,
  private val insertMyFileUseCase: InsertMyFileUseCase,
  private val changeFilePriorityUseCase: ChangeFilePriorityUseCase,
  private val changeSortOrderUseCase: ChangeSortOrderUseCase,
  private val clearDbUseCase: ClearDbUseCase,
  private val deletionSettingsActionChannel: Channel<FileSettingsAction>,
  private val setDeleteFilesUseCase: SetDeleteFilesUseCase,
  getSortOrderUseCase: GetSortOrderUseCase,
  getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {
  val deletionSettingsActionFlow = deletionSettingsActionChannel.receiveAsFlow()

  val sortOrderFlow = getSortOrderUseCase()

  val isFileDeletionEnabled = getSettingsUseCase().map { it.deleteFiles }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    false
  )

  val autoDeletionDataState =
    getFilesDbUseCase().map { DeletionDataState.ViewData(it) }
      .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DeletionDataState.Loading
      )

  fun removeFileFromDb(uri: Uri) {
    viewModelScope.launch {
      deleteMyFileUseCase(uri)
    }
  }

  fun showHelp() {
    viewModelScope.launch {
      deletionSettingsActionChannel.send(
        FileSettingsAction.ShowUsualDialog(
          DialogActions.ShowInfoDialog(
            UIText.StringResource(
              R.string.help
            ),
            UIText.StringResource(R.string.long_help_files)
          )
        )
      )
    }
  }

  fun showFileInfo(file: FileDomain) {
    viewModelScope.launch {
      with(file) {
        deletionSettingsActionChannel.send(
          FileSettingsAction.ShowUsualDialog(
            DialogActions.ShowInfoDialog(
              UIText.StringResource(
                R.string.about_file_title
              ), UIText.StringResource(R.string.fileDetails, name, priority, sizeFormatted)
            )
          )
        )
      }
    }
  }

  fun showPriorityEditor(file: FileDomain) {
    viewModelScope.launch {
      deletionSettingsActionChannel.send(
        FileSettingsAction.ShowPriorityEditor(
          UIText.StringResource(R.string.changePriority),
          file.priority.toString(),
          UIText.StringResource(R.string.file, file.name),
          file.uri.toString(),
          0..10000
        )
      )
    }
  }

  fun showClearDialog() {
    viewModelScope.launch {
      deletionSettingsActionChannel.send(
        FileSettingsAction.ShowUsualDialog(
          DialogActions.ShowQuestionDialog(
            UIText.StringResource(R.string.apply),
            UIText.StringResource(R.string.want_to_clear),
            CONFIRM_CLEAR_REQUEST
          )
        )
      )
    }
  }


  fun addFileToDb(uri: Uri, isDirectory: Boolean) {
    viewModelScope.launch {
      insertMyFileUseCase(uri, isDirectory)
    }
  }

  fun changeFilePriority(priority: Int, uri: Uri) {
    viewModelScope.launch {
      changeFilePriorityUseCase(priority, uri)
    }
  }


  fun clearFilesDb() {
    viewModelScope.launch {
      clearDbUseCase()
    }
  }

  fun changeSortOrder(sortOrder: FilesSortOrder) {
    viewModelScope.launch {
      changeSortOrderUseCase(sortOrder)
    }
  }

  fun changeFilesDeletionEnabled() {
    viewModelScope.launch {
      if (isFileDeletionEnabled.value) {
        changeDeletionEnabled()
        return@launch
      }
      deletionSettingsActionChannel.send(
        FileSettingsAction.ShowUsualDialog(
          DialogActions.ShowQuestionDialog(
            UIText.StringResource(R.string.enable_files_deletion),
            UIText.StringResource(R.string.enable_deletion_long),
            CHANGE_FILES_DELETION_REQUEST
          )
        )
      )

    }
  }

  fun changeDeletionEnabled() {
    viewModelScope.launch {
      setDeleteFilesUseCase(!isFileDeletionEnabled.value)
    }
  }


  companion object {
    const val CONFIRM_CLEAR_REQUEST = "confirm_clear_request"
    const val CHANGE_FILES_DELETION_REQUEST = "change_files_deletion_request"
  }

}
