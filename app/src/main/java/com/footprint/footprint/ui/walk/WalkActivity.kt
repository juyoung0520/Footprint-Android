package com.footprint.footprint.ui.walk

import android.os.Bundle
import android.util.Log
import com.footprint.footprint.R
import com.footprint.footprint.data.model.UserInfoModel
import com.footprint.footprint.data.model.UserModel
import com.footprint.footprint.databinding.ActivityWalkBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.utils.LogUtils
import com.google.gson.Gson
import com.skydoves.balloon.*

class WalkActivity : BaseActivity<ActivityWalkBinding>(ActivityWalkBinding::inflate) {
    var userInfo: UserInfoModel?= null
    override fun initAfterBinding() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToastMessage()

        // -> 사용자 정보 받아오기
        if (intent.hasExtra("userInfo")) {
            val userInfoJson = intent.getStringExtra("userInfo")
            userInfo = Gson().fromJson(userInfoJson, UserInfoModel::class.java)

            if (userInfo!!.weight == 0) {
                userInfo!!.weight = if (userInfo!!.gender == "male") 72 else 56
            }
            LogUtils.d("userInfo", userInfo.toString())
        }

        //취소 텍스트뷰 클릭 리스너 -> 실시간 기록을 중지할까요? 다이얼로그 화면 띄우기
        binding.walkCancelTv.setOnClickListener {
            showStopWalkDialog()
        }
    }

    //뒤로 가기 이벤트 -> 실시간 기록을 중지할까요? 다이얼로그 화면 띄우기
    override fun onBackPressed() {
        showStopWalkDialog()
    }

    //실시간 기록을 중지할까요? 다이얼로그 띄우는 함수
    private fun showStopWalkDialog() {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", getString(R.string.msg_stop_realtime_record))
        bundle.putString("action", getString(R.string.action_stop))

        val actionDialogFragment: ActionDialogFragment = ActionDialogFragment()
        actionDialogFragment.arguments = bundle

        actionDialogFragment.show(supportFragmentManager, null)

        actionDialogFragment.setMyDialogCallback(object :
            ActionDialogFragment.MyDialogCallback {

            //중지 텍스트뷰를 클릭하면 -> 액티비티 종료
            override fun action1(isAction: Boolean) {
                if (isAction)
                    finish()
            }

            override fun action2(isAction: Boolean) {
            }
        })
    }

    private fun setToastMessage() {
        val toastCustom = Balloon.Builder(this)
            .setText(getString(R.string.msg_question))
            .setTextColor(getColor(R.color.white))
            .setWidth(250)
            .setHeight(55)
            .setMarginRight(15)
            .setTextTypeface(R.font.namusquareround)
            .setIsVisibleArrow(true)
            .setArrowSize(8)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setArrowPosition(0.9f)
            .setAutoDismissDuration(3000L)
            .setCornerRadius(40F)
            .setBackgroundColorResource(R.color.black_80)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .build()

        binding.walkHelpIv.setOnClickListener {
            binding.walkHelpIv.showAlignBottom(toastCustom, 0, 0)
        }

        toastCustom.setOnBalloonClickListener {
            toastCustom.dismiss()
        }
    }
}