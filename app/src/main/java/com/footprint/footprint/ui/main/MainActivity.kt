package com.footprint.footprint.ui.main

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityMainBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.NewBadgeDialogFragment

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var navHostFragment: NavHostFragment

    override fun initAfterBinding() {
        initBottomNavigation()
        showMonthBadge()
    }

    private fun initBottomNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController: NavController = navHostFragment.findNavController()

        binding.mainBottomNavigation.setupWithNavController(navController)
        binding.mainBottomNavigation.itemIconTintList = null
    }

    private fun showMonthBadge() {
        //뱃지 있으면 -> 뱃지 다이얼로그 띄우기
        if (intent.hasExtra("badge")) {
            val badgeInfo = intent.getStringExtra("badge")

            val bundle = Bundle()
            bundle.putString("badge", badgeInfo)

            val newBadgeDialogFragment = NewBadgeDialogFragment()
            newBadgeDialogFragment.arguments = bundle
            newBadgeDialogFragment.show(supportFragmentManager, null)
        }
    }
}