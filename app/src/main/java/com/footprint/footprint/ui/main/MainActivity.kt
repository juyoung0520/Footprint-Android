package com.footprint.footprint.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.databinding.ActivityMainBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.NewBadgeDialogFragment
import com.footprint.footprint.ui.dialog.NoticeDialogFragment
import com.footprint.footprint.ui.dialog.TempWalkDialogFragment
import com.footprint.footprint.ui.walk.WalkAfterActivity
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private val mainVm: MainViewModel by viewModel()
    private lateinit var networkErrSb: Snackbar

    private lateinit var noticeDialogFragment: NoticeDialogFragment
    private lateinit var newBadgeDialogFragment:NewBadgeDialogFragment
    private lateinit var tempWalkDialog: TempWalkDialogFragment //임시 저장 산책 다이얼로그

    private val notices: ArrayList<NoticeDto> = arrayListOf() //주요 공지사항 목록들

    override fun initAfterBinding() {
        initBottomNavigation()
        initTempWalkDialog()
        initDialog()
        observe()

        /* (1) 공지사항 확인 */
        mainVm.getKeyNotice()
    }

    private fun initBottomNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

        navController = navHostFragment.findNavController()
        setChangeListener()

        binding.mainBottomNavigation.setupWithNavController(navController)
        binding.mainBottomNavigation.itemIconTintList = null
    }

    private fun setChangeListener(){
        navController.addOnDestinationChangedListener { _, _, _ ->

            // 홈화면에서 아직 확인하지 않은 주요 공지사항이 있다면 모달을 띄워줌
            val isHomeFragment = navHostFragment.findNavController().currentDestination!!.id == R.id.homeFragment
            if (isHomeFragment && notices.isNotEmpty()){
                showKeyNotice()
            }
        }
    }

    private fun initDialog(){
        // 공지사항 다이얼로그
        noticeDialogFragment = NoticeDialogFragment()
        noticeDialogFragment.setMyDialogCallback(object : NoticeDialogFragment.MyDialogCallback{
            override fun isDismissed() {
                if(notices.isNotEmpty()){
                    showKeyNotice()
                }
                else{ /* 1-2) 공지사항을 다 띄워준 경우 -> (2) 이달의 뱃지 확인 */
                    checkMonthBadge()
                }
            }
        })

        // 이달의 뱃지 다이얼로그
        newBadgeDialogFragment = NewBadgeDialogFragment()
        newBadgeDialogFragment.setMyDialogCallback(object : NewBadgeDialogFragment.MyDialogCallback{
            override fun confirm() { /* 2-2) 뱃지 확인 완료한 경우 -> (3) 임시 저장 산책 확인  */
                if (getTempWalk() != null)    //임시 저장된 산책 정보가 있으면 TempWalkDialog 띄우기
                    tempWalkDialog.show(supportFragmentManager, null)
            }
        })
    }

    private fun checkMonthBadge(){
        if (intent.hasExtra("badgeCheck") && intent.getBooleanExtra("badgeCheck", false)){
            mainVm.getMonthBadge()
        }else{ /* (1) 확인할 뱃지가 없는 경우 -> 3) 임시저장 확인 */
            if (getTempWalk() != null)    //임시 저장된 산책 정보가 있으면 TempWalkDialog 띄우기
                tempWalkDialog.show(supportFragmentManager, null)
        }
    }

    /* Show Dialog */
    private fun showMonthBadge(badgeInfo: String) {
        val bundle = Bundle()
        bundle.putString("badge", badgeInfo)

        newBadgeDialogFragment.arguments = bundle
        newBadgeDialogFragment.show(supportFragmentManager, null)
    }

    private fun showKeyNotice() {
        if (navHostFragment.findNavController().currentDestination!!.id == R.id.homeFragment){
            val notice = notices.removeAt(0)
            val bundle = Bundle()
            bundle.putString("notice", Gson().toJson(notice))

            noticeDialogFragment.arguments = bundle
            noticeDialogFragment.show(supportFragmentManager, null)
        }
    }

    //임시 저장 산책 기록 다이얼로그 초기화 함수
    private fun initTempWalkDialog() {
        tempWalkDialog = TempWalkDialogFragment()

        tempWalkDialog.setMyCallbackListener(object : TempWalkDialogFragment.MyCallbackListener {
            override fun delete() {
                removeTempWalk()    //임시 저장해 놨던 산책 기록 데이터 삭제
            }

            override fun followUp() {
                val walkAfterIntent = Intent(this@MainActivity, WalkAfterActivity::class.java)
                walkAfterIntent.putExtra("walk", getTempWalk())    //산책 정보 전달
                startActivity(walkAfterIntent)
            }
        })
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
                ErrorType.NO_BADGE -> { /* 2-1) 뱃지가 없는 경우 -> (3) 임시 저장 산책 확인  */
                    if (getTempWalk() != null)    //임시 저장된 산책 정보가 있으면 TempWalkDialog 띄우기
                        tempWalkDialog.show(supportFragmentManager, null)
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

            if(it.keyNoticeList.isEmpty()){ /* 1-1) 공지사항이 없는 경우 -> (2) 이달의 뱃지 확인 */
                checkMonthBadge()
            }else{
                notices.addAll(it.keyNoticeList)
                showKeyNotice()
            }

        })
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }

}