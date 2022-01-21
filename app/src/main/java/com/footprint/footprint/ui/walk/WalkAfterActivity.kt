package com.footprint.footprint.ui.walk

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityWalkAfterBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.ActionDialogFragment

class WalkAfterActivity :
    BaseActivity<ActivityWalkAfterBinding>(ActivityWalkAfterBinding::inflate) {
    private lateinit var actionDialogFragment: ActionDialogFragment
    private lateinit var navHostFragment: NavHostFragment

    override fun initAfterBinding() {
        setMyClickListener()
        setActionDialog()

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.walk_after_nav_host_fragment_container) as NavHostFragment
        val navController: NavController = navHostFragment.findNavController()

        val footprints = intent.getStringExtra("footprints")
        val bundle = Bundle()
        if (footprints == null)
            bundle.putString("footprints", "")
        else
            bundle.putString("footprints", footprints)
        navController.setGraph(R.navigation.navigation_walk_confirm, bundle)

        val heightPixels = resources.displayMetrics.heightPixels.toFloat()
        binding.walkAfterTb.layoutParams.height = (heightPixels * 0.14).toInt()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun setMyClickListener() {
        //취소 텍스트뷰 클릭 리스너
        binding.walkAfterCancelTv.setOnClickListener {
            //‘OO번째 산책’ 작성을 취소할까요? 다이얼로그 화면 띄우기
            setWalkDialogBundle(getString(R.string.msg_stop_walk))
            actionDialogFragment.show(
                supportFragmentManager,
                null
            )
        }

        //저장 텍스트뷰 클릭 리스너
        binding.walkAfterSaveTv.setOnClickListener {
            //‘OO번째 산책’을 저장할까요? 다이얼로그 화면 띄우기
            setWalkDialogBundle(getString(R.string.msg_save_walk))
            actionDialogFragment.show(
                supportFragmentManager,
                null
            )
        }
    }

    //WalkDialogFragment 초기화
    private fun setActionDialog() {
        actionDialogFragment = ActionDialogFragment()

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun finish(isFinished: Boolean) {
                if (isFinished)
                    finish()
            }

            override fun save(isSaved: Boolean) {
                if (isSaved)
                    finish()
            }

            override fun delete(isDelete: Boolean) {
            }
        })
    }

    //WalkDialogFragment 에 넘겨준 메세지(ex.‘OO번째 산책’을 저장할까요?)를 저장하는 함수
    private fun setWalkDialogBundle(msg: String) {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", msg)

        actionDialogFragment.arguments = bundle
    }
}