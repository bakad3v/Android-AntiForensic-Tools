package com.sonozaki.files.presentation.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sonozaki.dialogs.QuestionDialog
import com.sonozaki.entities.FilesSortOrder
import com.sonozaki.files.R
import com.sonozaki.files.databinding.SetupUsualFilesFragmentBinding
import com.sonozaki.files.presentation.adapter.FileAdapter
import com.sonozaki.files.presentation.state.FileDataState
import com.sonozaki.files.presentation.viewmodel.UsualFilesSettingsVM
import com.sonozaki.files.presentation.viewmodel.UsualFilesSettingsVM.Companion.CHANGE_FILES_DELETION_REQUEST
import com.sonozaki.files.presentation.viewmodel.UsualFilesSettingsVM.Companion.CONFIRM_CLEAR_REQUEST
import com.sonozaki.utils.TopLevelFunctions.launchLifecycleAwareCoroutine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Fragment for setting up usual files list
 */
@AndroidEntryPoint
class SetupFilesFragment : Fragment() {
  private val viewModel: UsualFilesSettingsVM by viewModels()
  private var _binding: SetupUsualFilesFragmentBinding? = null
  private val binding
    get() = _binding ?: throw RuntimeException("DeletionSettingsFragmentBinding == null")
  private val takeFlags by lazy {
    Intent.FLAG_GRANT_READ_URI_PERMISSION or
      Intent.FLAG_GRANT_WRITE_URI_PERMISSION
  }
  private val dialogLauncher by lazy {
      com.sonozaki.dialogs.DialogLauncher(
          parentFragmentManager,
          context
      )
  }

  @Inject
  lateinit var myFileAdapterFactory: FileAdapter.Factory

  private val myFileAdapter: FileAdapter by lazy {
    myFileAdapterFactory.create(
      onMoreClickListener = { viewModel.showFileInfo(it) },
      onDeleteItemClickListener = { viewModel.removeFileFromDb(it) },
      onEditItemClickListener = { viewModel.showPriorityEditor(it) }
    )
  }


  private val fileSelectionLauncher =
    registerForActivityResult(ActivityResultContracts.OpenDocument()) { file ->

      if (file == null) {
        return@registerForActivityResult
      }
      requireActivity().grantUriPermission(
        requireActivity().packageName,
        file,
        takeFlags
      )
      requireActivity().contentResolver.takePersistableUriPermission(file, takeFlags)
      try {
        viewModel.addFileToDb(file, false)
      } catch (e: Exception) {
        Toast.makeText(
          requireContext(),
          getString(R.string.file_removal_unsuccessfull),
          Toast.LENGTH_LONG
        )
          .show()
      }
      viewModel.changeFABsVisibility()
    }

  private val folderSelectionLauncher =
    registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { folder ->
      if (folder == null) {
        return@registerForActivityResult
      }
      requireActivity().grantUriPermission(
        requireActivity().packageName,
        folder,
        takeFlags
      )
      requireActivity().contentResolver.takePersistableUriPermission(folder, takeFlags)
      try {
        viewModel.addFileToDb(folder, true)
      } catch (e: Exception) {
        Toast.makeText(
          requireContext(),
          getString(R.string.folder_removal_unsuccessful),
          Toast.LENGTH_LONG
        )
          .show()
      }
      viewModel.changeFABsVisibility()
    }

  private val requestFilesPermissionLauncher =
    registerForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) { granted ->
      if (!granted)
        Toast.makeText(
          requireContext(),
          getString(R.string.access_denied),
          Toast.LENGTH_SHORT
        ).show()
    }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding =
      SetupUsualFilesFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    checkFilesPermissions()
    setupRecyclerView()
    setupFABs()
    setupSort()
    setupFilesListListener()
    setupDialogListeners()
    setupActionsListener()
    setMainActivityState()
    setupMenu()
    observeSortOrder()
  }

  /**
   * Requesting permission for files reading. Necessary for calculating folders sizes.
   */
  private fun checkFilesPermissions() {
    if (ContextCompat.checkSelfPermission(
        requireActivity(),
        Manifest.permission.READ_EXTERNAL_STORAGE
      )
      != PackageManager.PERMISSION_GRANTED
      && Build.VERSION.SDK_INT < 33
    ) {
      requestFilesPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
  }

  /**
   * Setting up sorting text
   */
  private fun observeSortOrder() {
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.sortOrderFlow.collect {
        binding.sort.text = when (it) {
          FilesSortOrder.NAME_ASC -> resources.getString(R.string.alphabet)
          FilesSortOrder.NAME_DESC -> resources.getString(R.string.disalphabet)
          FilesSortOrder.PRIORITY_ASC -> resources.getString(R.string.minpr)
          FilesSortOrder.PRIORITY_DESC -> resources.getString(R.string.maxpr)
          FilesSortOrder.SIZE_DESC -> resources.getString(R.string.sizebig)
          FilesSortOrder.SIZE_ASC -> resources.getString(R.string.sizesmall)
        }
      }
    }
  }

  /**
   * Rendering button for enabling or disabling file deletion
   */
  private suspend fun Menu.drawSwitchFileDeletionStatusButton() {
    viewModel.isFileDeletionEnabled.collect {
      val icon: Int
      val text: Int
      if (it) {
        icon = com.sonozaki.resources.R.drawable.ic_baseline_pause_24
        text = R.string.disable_files_deletion
      } else {
        icon = com.sonozaki.resources.R.drawable.ic_baseline_play_arrow_24
        text = R.string.enable_files_deletion
      }
      withContext(Dispatchers.Main) {
        val startIcon = findItem(R.id.enable)
          ?: throw RuntimeException("Enable files button not found")
        startIcon.setIcon(icon).setTitle(text)
      }
    }
  }

  /**
   * Setting up menu
   */
  private fun setupMenu() {
    requireActivity().addMenuProvider(object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        viewLifecycleOwner.launchLifecycleAwareCoroutine {
          menuInflater.inflate(R.menu.files_menu, menu)
          menu.drawSwitchFileDeletionStatusButton()
        }
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
          R.id.help -> viewModel.showHelp()
          R.id.clear -> viewModel.showClearDialog()
          R.id.enable -> viewModel.changeFilesDeletionEnabled()
        }
        return true
      }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
  }

  private fun setMainActivityState() {
    val activity = requireActivity()
    if (activity is com.sonozaki.activitystate.ActivityStateHolder)
      activity.setActivityState(
        com.sonozaki.activitystate.ActivityState.NormalActivityState(
          getString(
            R.string.file_deletion_settings
          )
        )
      )
  }

  /**
   * Setting up dialog launcher
   */
  private fun setupActionsListener() {
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.deletionSettingsActionFlow.collect {
        when (it) {
          is com.sonozaki.files.presentation.actions.FileSettingsAction.ShowUsualDialog -> dialogLauncher.launchDialogFromAction(it.value)
          is com.sonozaki.files.presentation.actions.FileSettingsAction.ShowPriorityEditor -> {
            with(it) {
              showPriorityEditor(
                title.asString(context),
                hint,
                message.asString(context),
                uri,
                range
              )
            }
          }
        }
      }
    }
  }

  private fun showPriorityEditor(
    title: String,
    hint: String,
    message: String,
    uri: String,
    range: IntRange
  ) {
    com.sonozaki.dialogs.InputDigitDialog.showPriorityEditor(parentFragmentManager, title, hint, message, uri, range)
  }


  /**
   * Sending data to adapter
   */
  private fun setupFilesListListener() {
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.autoFileDataState.collect {
        changeFABSVisibility(it.expandedFABS)
        when(it) {
          is FileDataState.ViewData -> {
            setFilesVisibility(true)
            myFileAdapter.submitList(it.items)
          }
          is FileDataState.Loading -> setFilesVisibility(false)
        }
      }
    }
  }

  private fun setFilesVisibility(visible: Boolean) {
    with(binding) {
      items.visibility = com.sonozaki.utils.booleanToVisibility(visible)
      progressBar2.visibility = com.sonozaki.utils.booleanToVisibility(!visible)
    }
  }

  /**
   * Listening to dialogs results
   */
  private fun setupDialogListeners() {
    com.sonozaki.dialogs.InputDigitDialog.setupEditPriorityListener(
      parentFragmentManager,
      viewLifecycleOwner
    ) { uri: Uri, priority: Int ->
      viewModel.changeFilePriority(priority, uri)
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      CONFIRM_CLEAR_REQUEST,
      viewLifecycleOwner
    ) {
      viewModel.clearFilesDb()
    }
    QuestionDialog.setupListener(
      parentFragmentManager,
      CHANGE_FILES_DELETION_REQUEST,
      viewLifecycleOwner
    ) {
      viewModel.changeDeletionEnabled()
    }
  }

  private fun setupSort() {
    binding.sort.setOnClickListener {
      showSortingMenu()
    }
  }
  /**
   * Setting recyclerview
   */
  private fun setupRecyclerView() {
    with(binding.items) {
      layoutManager = LinearLayoutManager(context)
      adapter = myFileAdapter
    }
  }

  /**
   * Setting up floating action buttons
   */
  private fun setupFABs() {
    binding.add.setOnClickListener {
      viewModel.changeFABsVisibility()
    }
    setupAddFolderButton()
    setupAddFileButton()
  }

  private fun setupAddFileButton() {
    binding.addFile.setOnClickListener {
      fileSelectionLauncher.launch(arrayOf("*/*"))
    }
  }

  private fun setupAddFolderButton() {
    binding.addFolder.setOnClickListener {
      val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
      intent.addFlags(
        Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
      )
      folderSelectionLauncher.launch(intent.data)
    }
  }

  /**
   * Changing visibility of additional floating action buttons
   */
  private fun changeFABSVisibility(extended: Boolean) {
    if (!extended) {
      binding.addFile.hide()
      binding.addFolder.hide()
      binding.add.shrink()
    } else {
      binding.add.extend()
      binding.addFile.show()
      binding.addFolder.show()
    }
  }

  /**
   * Showing popup for selecting sort order
   */
  private fun showSortingMenu() {
    val popup = PopupMenu(context, binding.sort)
    popup.menuInflater.inflate(R.menu.sorting, popup.menu)
    popup.setOnMenuItemClickListener {
      val priority = when (it.itemId) {
        R.id.maxpriority -> com.sonozaki.entities.FilesSortOrder.PRIORITY_DESC

        R.id.minpriority -> com.sonozaki.entities.FilesSortOrder.PRIORITY_ASC

        R.id.alphabet -> com.sonozaki.entities.FilesSortOrder.NAME_ASC

        R.id.desalphabet -> FilesSortOrder.NAME_DESC

        R.id.maxsize -> FilesSortOrder.SIZE_DESC

        R.id.minsize -> FilesSortOrder.SIZE_ASC

        else -> throw RuntimeException("Wrong priority in priority sorting")
      }
      viewModel.changeSortOrder(priority)
      return@setOnMenuItemClickListener true
    }
    popup.show()
  }



  override fun onDestroyView() {
    binding.items.setAdapter(null)
    _binding = null
    super.onDestroyView()
  }

}
