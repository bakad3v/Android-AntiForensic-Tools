package com.android.aftools.presentation.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.android.aftools.R
import com.android.aftools.TopLevelFunctions.launchLifecycleAwareCoroutine
import com.android.aftools.databinding.MainActivityBinding
import com.android.aftools.presentation.states.ActivityState
import com.android.aftools.presentation.viewmodels.MainVM
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityStateHolder {
    private lateinit var controller: NavController
    private var _mainBinding: MainActivityBinding? = null
    private val mainBinding
        get() = _mainBinding ?: throw RuntimeException("MainActivityBinding == null")
    private val viewModel: MainVM by viewModels()
    private var drawerReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        observeTheme()
        _mainBinding =
            MainActivityBinding.inflate(layoutInflater)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment)
                    as NavHostFragment
        controller = navHostFragment.navController
        setContentView(mainBinding.root)
        observeActivityState()
    }

    /**
     * Function for changing app theme according to user's settings
     */
    private fun observeTheme() {
        launchLifecycleAwareCoroutine {
            viewModel.theme.collect {
                AppCompatDelegate.setDefaultNightMode(it.asMode())
            }
        }
    }

    /**
     * Function for setting up navigation. If screen size is large, navigation drawer is visible.
     */
    private fun setupDrawer() {
        mainBinding.navigationView.inflateMenu(R.menu.main_menu)
        mainBinding.navigationView.setupWithNavController(controller)
    }

    /**
     * Function for setting activity state from fragments
     */
    override fun setActivityState(state: ActivityState) {
        viewModel.setActivityState(state)
    }

    /**
     * Function for changing toolbar and navigation drawer settings after opening new fragment
     */
    private fun observeActivityState() {
        launchLifecycleAwareCoroutine {
            viewModel.activityState.collect {
                when (it) {
                    is ActivityState.NoActionBarActivityState -> setupNoActionBarFragment()
                    is ActivityState.NormalActivityState -> setupNormalFragment(it)
                    is ActivityState.NoActionBarNoDrawerActivityState -> setupNoActionBarNoDrawerFragment()
                }
            }
        }
    }

    private fun setupNoActionBarFragment() {
        mainBinding.toolbar2.menu.clear()
        setSupportActionBar(null)
        mainBinding.toolbar2.visibility = View.GONE
    }

    private fun setupNormalFragment(state: ActivityState.NormalActivityState) {
        mainBinding.toolbar2.visibility = View.VISIBLE
        mainBinding.toolbar2.title = state.title
        if (supportActionBar == null) {
            setSupportActionBar(mainBinding.toolbar2)
        }
        mainBinding.navigationView.visibility = View.VISIBLE
        setupToolbarMenu()
        if (!drawerReady) {
            setupDrawer()
            drawerReady = true
        }
    }

    private fun setupToolbarMenu() {
        if (mainBinding.bigLayout == null) {
            val conf = AppBarConfiguration(
                setOf(
                    R.id.settingsFragment,
                    R.id.profilesFragment,
                    R.id.rootFragment,
                    R.id.setupFilesFragment,
                    R.id.logsFragment,
                    R.id.aboutFragment
                ), mainBinding.drawer
            )
            NavigationUI.setupWithNavController(mainBinding.toolbar2, controller, conf)
        }
    }

    private fun setupNoActionBarNoDrawerFragment() {
        mainBinding.toolbar2.menu.clear()
        setSupportActionBar(null)
        mainBinding.navigationView.visibility = View.GONE
        mainBinding.toolbar2.visibility = View.GONE
    }

    override fun onDestroy() {
        _mainBinding = null
        super.onDestroy()
    }

}
