package com.footprint.footprint.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.KeyNoticeDto
import com.footprint.footprint.databinding.ActivityMainBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.NewBadgeDialogFragment
import com.footprint.footprint.ui.dialog.NoticeDialogFragment
import com.footprint.footprint.ui.dialog.NoticeDialogFragmentDirections
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.isReadNotice
import com.footprint.footprint.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var navHostFragment: NavHostFragment

    private val mainVm: MainViewModel by viewModel()
    private lateinit var noticeDialogFragment: NoticeDialogFragment

    override fun initAfterBinding() {
        initBottomNavigation()
        initNoticeDialog()

        checkBadgeExist()
        observe()
    }

    /* Init - BottomNavigation, Notice, Badge */
    private fun initBottomNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController: NavController = navHostFragment.findNavController()

        binding.mainBottomNavigation.setupWithNavController(navController)
        binding.mainBottomNavigation.itemIconTintList = null
    }

    private fun initNoticeDialog(){
        /* 테스트 - getKeyNotice */
        //mainVm.getKeyNotice() // onCreate 시, 1번만 부르면 됨

        noticeDialogFragment = NoticeDialogFragment()

        noticeDialogFragment.setMyDialogCallback(object : NoticeDialogFragment.MyDialogCallback{
            override fun detail(notice: KeyNoticeDto) {

                val action = NoticeDialogFragmentDirections.actionNoticeDialogToNoticeDetailFragment(notice.noticeIdx.toString())
                navHostFragment.findNavController().navigate(action) /* 이게 맞는지 모르겠음 - 테스트 */

            }
        })
    }

    private fun checkBadgeExist(){
        if (intent.hasExtra("badgeCheck") && intent.getBooleanExtra("badgeCheck", false)){
            //badgeCheck가 true면 badge API 호출
            mainVm.getMonthBange()
        }
    }

    /* Dialog */
    private fun showMonthBadge(badgeInfo: String) {
        val bundle = Bundle()
        bundle.putString("badge", badgeInfo)

        val newBadgeDialogFragment = NewBadgeDialogFragment()
        newBadgeDialogFragment.arguments = bundle
        newBadgeDialogFragment.show(supportFragmentManager, null)
    }

    private fun showKeyNotice(notice: String) {
        val bundle = Bundle()
        bundle.putString("notice", notice)

        noticeDialogFragment.arguments = bundle
        noticeDialogFragment.show(supportFragmentManager, null)
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

        mainVm.thisKeyNoticeList.observe(this, Observer {
            for(notice in it){
                /* 안읽은 중요 공지사항 많으면 에바일 거 같은디.. 테스트 필요 */
                if(!isReadNotice(notice.noticeIdx)){
                    val jsonNotice = Gson().toJson(notice)
                    showKeyNotice(jsonNotice)
                }
            }
        })
    }
}