package com.footprint.footprint.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.databinding.ActivityMainBinding
import com.footprint.footprint.domain.model.Badge
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.NewBadgeDialogFragment
import com.footprint.footprint.ui.dialog.NoticeDialogFragment
import com.footprint.footprint.ui.error.ErrorActivity
import com.footprint.footprint.ui.main.home.HomeFragmentDirections
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.internal.wait
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var navHostFragment: NavHostFragment

    private val mainVm: MainViewModel by viewModel()
    private lateinit var networkErrSb: Snackbar

    private lateinit var noticeDialogFragment: NoticeDialogFragment
    private val acquireNotices: ArrayList<NoticeDto> = arrayListOf() //주요 공지사항 목록들

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
        mainVm.getKeyNotice()

        noticeDialogFragment = NoticeDialogFragment()
        noticeDialogFragment.setMyDialogCallback(object : NoticeDialogFragment.MyDialogCallback{
            override fun isDismissed() {
                if(acquireNotices.isNotEmpty())
                    showKeyNotice(acquireNotices.removeAt(0))
            }

            override fun showingDetail() { // 자세히 보기 버튼을 통해 Detail로 이동 시, 마이페이지로 바텀 아이콘 이동
                binding.mainBottomNavigation.menu.findItem(R.id.mypageFragment).isChecked = true
            }

        })
    }

    private fun checkBadgeExist(){
        if (intent.hasExtra("badgeCheck") && intent.getBooleanExtra("badgeCheck", false)){
            //badgeCheck가 true면 badge API 호출
            mainVm.getMonthBadge()
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

    private fun showKeyNotice(notice: NoticeDto) {
        val bundle = Bundle()
        bundle.putString("notice", Gson().toJson(notice))

        noticeDialogFragment.arguments = bundle
        noticeDialogFragment.show(supportFragmentManager, null)
    }

    private fun observe(){
        mainVm.mutableErrorType.observe(this, androidx.lifecycle.Observer {
            when (it) {
                ErrorType.NETWORK -> {
                    networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE)

                    when(mainVm.getErrorType()){
                        "getMonthBadge" -> networkErrSb.setAction(R.string.action_retry) { mainVm.getMonthBadge() }
                        "getKeyNotice" -> networkErrSb.setAction(R.string.action_retry) { mainVm.getKeyNotice() }
                    }
                    networkErrSb.show()
                }
                ErrorType.NO_BADGE -> { // 이번 달에 획득한 뱃지가 없습니다 -> 무시
                    LogUtils.d("Main", "이번 달에 획득한 뱃지가 없습니다")
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    startErrorActivity("MainActivity")
                }
            }
        })

        mainVm.thisMonthBadge.observe(this, Observer {
           val badgeInfo = Gson().toJson(mainVm.thisMonthBadge.value)
            showMonthBadge(badgeInfo)
        })

        mainVm.thisKeyNoticeList.observe(this, Observer {
            acquireNotices.addAll(it.keyNoticeList)

            if(acquireNotices.isNotEmpty())
                showKeyNotice(acquireNotices.removeAt(0))
        })
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }
}