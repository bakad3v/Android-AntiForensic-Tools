package com.sonozaki.files.presentation.viewmodel


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonozaki.dialogs.DialogActions
import com.sonozaki.entities.FilesSortOrder
import com.sonozaki.files.R
import com.sonozaki.files.domain.entities.FileInfo
import com.sonozaki.files.domain.usecases.ChangeFilePriorityUseCase
import com.sonozaki.files.domain.usecases.ChangeSortOrderUseCase
import com.sonozaki.files.domain.usecases.ClearFilesUseCase
import com.sonozaki.files.domain.usecases.DeleteMyFileUseCase
import com.sonozaki.files.domain.usecases.GetFilesDeletionEnabledUseCase
import com.sonozaki.files.domain.usecases.GetFilesUseCase
import com.sonozaki.files.domain.usecases.GetSortOrderUseCase
import com.sonozaki.files.domain.usecases.InsertMyFileUseCase
import com.sonozaki.files.domain.usecases.SetDeleteFilesUseCase
import com.sonozaki.files.presentation.actions.FileSettingsAction
import com.sonozaki.files.presentation.state.FileDataState
import com.sonozaki.utils.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsualFilesSettingsVM @Inject constructor(
  getFilesUseCase: GetFilesUseCase,
  private val deleteMyFileUseCase: DeleteMyFileUseCase,
  private val insertMyFileUseCase: InsertMyFileUseCase,
  private val changeFilePriorityUseCase: ChangeFilePriorityUseCase,
  private val changeSortOrderUseCase: ChangeSortOrderUseCase,
  private val clearFilesUseCase: ClearFilesUseCase,
  private val deletionSettingsActionChannel: Channel<FileSettingsAction>,
  private val setDeleteFilesUseCase: SetDeleteFilesUseCase,
  private val buttonsExpandedFlow: MutableStateFlow<Boolean>,
  getSortOrderUseCase: GetSortOrderUseCase,
  getFilesDeletionEnabledUseCase: GetFilesDeletionEnabledUseCase,
) : ViewModel() {

  val deletionSettingsActionFlow = deletionSettingsActionChannel.receiveAsFlow()

  val sortOrderFlow = getSortOrderUseCase()

  val isFileDeletionEnabled = getFilesDeletionEnabledUseCase().stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(0, 0),
    false
  )

  val autoFileDataState =
    combine(getFilesUseCase(), buttonsExpandedFlow) { files, expandedFABS -> FileDataState.ViewData(files, expandedFABS) }
      .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0, 0),
        FileDataState.Loading()
      )

  fun removeFileFromDb(uri: Uri) {
    viewModelScope.launch {
      deleteMyFileUseCase(uri)
    }
  }

  fun changeFABsVisibility() {
    viewModelScope.launch {
      buttonsExpandedFlow.getAndUpdate {
        !it
      }
    }
  }

  fun showHelp() {
    viewModelScope.launch {
      deletionSettingsActionChannel.send(
        FileSettingsAction.ShowUsualDialog(
          DialogActions.ShowInfoDialog(
            UIText.StringResource(
              com.sonozaki.resources.R.string.help
            ),
            UIText.StringResource(R.string.long_help_files)
          )
        )
      )
    }
  }

  fun showFileInfo(file: FileInfo) {
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

  fun showPriorityEditor(file: FileInfo) {
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
      clearFilesUseCase()
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
