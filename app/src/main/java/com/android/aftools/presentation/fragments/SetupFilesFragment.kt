package com.android.aftools.presentation.fragments

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
import com.android.aftools.R
import com.android.aftools.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.android.aftools.databinding.SetupUsualFilesFragmentBinding
import com.android.aftools.domain.entities.FilesSortOrder
import com.android.aftools.presentation.actions.FileSettingsAction
import com.android.aftools.presentation.activities.MainActivity
import com.android.aftools.presentation.adapters.fileAdapter.FileAdapter
import com.android.aftools.presentation.dialogs.DialogLauncher
import com.android.aftools.presentation.dialogs.InputDigitDialog
import com.android.aftools.presentation.dialogs.QuestionDialog
import com.android.aftools.presentation.states.ActivityState
import com.android.aftools.presentation.states.DeletionDataState
import com.android.aftools.presentation.viewmodels.UsualFilesSettingsVM
import com.android.aftools.presentation.viewmodels.UsualFilesSettingsVM.Companion.CHANGE_FILES_DELETION_REQUEST
import com.android.aftools.presentation.viewmodels.UsualFilesSettingsVM.Companion.CONFIRM_CLEAR_REQUEST
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
  private var allFabsVisible = true
  private val binding
    get() = _binding ?: throw RuntimeException("DeletionSettingsFragmentBinding == null")
  private val contentResolver by lazy { requireContext().contentResolver }
  private val takeFlags by lazy {
    Intent.FLAG_GRANT_READ_URI_PERMISSION or
      Intent.FLAG_GRANT_WRITE_URI_PERMISSION
  }

  @Inject
  lateinit var myFileAdapter: FileAdapter


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
      changeVisibility()
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
      changeVisibility()
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
    binding.lifecycleOwner = viewLifecycleOwner
    binding.viewmodel = viewModel
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
    launchLifecycleAwareCoroutine {
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
        icon = R.drawable.ic_baseline_pause_24
        text = R.string.disable_files_deletion
      } else {
        icon = R.drawable.ic_baseline_play_arrow_24
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
        launchLifecycleAwareCoroutine {
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
    (activity as MainActivity).setActivityState(
      ActivityState.NormalActivityState(
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
    val dialogLauncher = DialogLauncher(parentFragmentManager, context)
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.deletionSettingsActionFlow.collect {
        when (it) {
          is FileSettingsAction.ShowUsualDialog -> dialogLauncher.launchDialogFromAction(it.value)
          is FileSettingsAction.ShowPriorityEditor -> {
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
    InputDigitDialog.showPriorityEditor(parentFragmentManager, title, hint, message, uri, range)
  }


  /**
   * Sending data to adapter
   */
  private fun setupFilesListListener() {
    viewLifecycleOwner.launchLifecycleAwareCoroutine {
      viewModel.autoDeletionDataState.collect {
        if (it is DeletionDataState.ViewData) {
          myFileAdapter.submitList(it.items)
        }
      }
    }
  }

  /**
   * Listening to dialogs results
   */
  private fun setupDialogListeners() {
    InputDigitDialog.setupEditPriorityListener(
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
   * Setting recyclerview buttons listeners
   */
  private fun FileAdapter.setRecyclerViewListeners() {
    onMoreClickListener = { viewModel.showFileInfo(it) }
    onDeleteItemClickListener = { viewModel.removeFileFromDb(it) }
    onEditItemClickListener = { viewModel.showPriorityEditor(it) }
  }

  /**
   * Setting recyclerview
   */
  private fun setupRecyclerView() {
    with(binding.items) {
      layoutManager = LinearLayoutManager(context)
      myFileAdapter.setRecyclerViewListeners()
      adapter = myFileAdapter
    }
  }

  /**
   * Setting up floating action buttons
   */
  private fun setupFABs() {
    changeVisibility()
    binding.add.setOnClickListener {
      changeVisibility()
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
  private fun changeVisibility() {
    if (allFabsVisible) {
      binding.addFile.hide()
      binding.addFolder.hide()
      binding.add.shrink()
    } else {
      binding.add.extend()
      binding.addFile.show()
      binding.addFolder.show()
    }
    allFabsVisible = !allFabsVisible
  }

  /**
   * Showing popup for selecting sort order
   */
  private fun showSortingMenu() {
    val popup = PopupMenu(context, binding.sort)
    popup.menuInflater.inflate(R.menu.sorting, popup.menu)
    popup.setOnMenuItemClickListener {
      val priority = when (it.itemId) {
        R.id.maxpriority -> FilesSortOrder.PRIORITY_DESC

        R.id.minpriority -> FilesSortOrder.PRIORITY_ASC

        R.id.alphabet -> FilesSortOrder.NAME_ASC

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
