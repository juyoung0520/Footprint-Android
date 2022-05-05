package com.footprint.footprint.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityMainBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.NewBadgeDialogFragment
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.removeJwt
import com.footprint.footprint.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var navHostFragment: NavHostFragment

    private val mainVm: MainViewModel by viewModel()

    override fun initAfterBinding() {
        initBottomNavigation()

        checkBadgeExist()
        observe()
    }

    private fun initBottomNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController: NavController = navHostFragment.findNavController()

        binding.mainBottomNavigation.setupWithNavController(navController)
        binding.mainBottomNavigation.itemIconTintList = null
    }

    private fun showMonthBadge(badgeInfo: String) {
        val bundle = Bundle()
        bundle.putString("badge", badgeInfo)

        val newBadgeDialogFragment = NewBadgeDialogFragment()
        newBadgeDialogFragment.arguments = bundle
        newBadgeDialogFragment.show(supportFragmentManager, null)
    }

    private fun checkBadgeExist(){
        if (intent.hasExtra("badgeCheck") && intent.getBooleanExtra("badgeCheck", false)){
            //badgeCheck가 true면 badge API 호출
            mainVm.getMonthBange()
        }
    }

    private fun observe(){
        mainVm.mutableErrorType.observe(this, androidx.lifecycle.Observer {
            when (it) {
                ErrorType.NETWORK -> {
                    Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(
                        R.string.action_retry) {
                        mainVm.getMonthBange()
                    }.show()
                }
                else -> Snackbar.make(binding.root, getString(R.string.error_api_fail), Snackbar.LENGTH_INDEFINITE).setAction(
                    R.string.action_retry) {
                    mainVm.getMonthBange()
                }.show()
            }
        })

        mainVm.thisMonthBadge.observe(this, Observer {
           val badgeInfo = Gson().toJson(mainVm.thisMonthBadge.value)
            showMonthBadge(badgeInfo)
        })
    }
}