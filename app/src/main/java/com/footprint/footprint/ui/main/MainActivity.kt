package com.footprint.footprint.ui.main

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityMainBinding
import com.footprint.footprint.ui.BaseActivity

class MainActivity: BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){
    private lateinit var navHostFragment: NavHostFragment

    override fun initAfterBinding() {
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController: NavController = navHostFragment.findNavController()

        binding.mainBottomNavigation.setupWithNavController(navController)
        binding.mainBottomNavigation.itemIconTintList = null
    }
}