package com.footprint.footprint.ui.walk

import android.os.Bundle
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityWalkBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.ActionDialogFragment

class WalkActivity : BaseActivity<ActivityWalkBinding>(ActivityWalkBinding::inflate) {
    override fun initAfterBinding() {
        //취소 텍스트뷰 클릭 리스너 -> 실시간 기록을 중지할까요? 다이얼로그 화면 띄우기
        binding.walkCancelTv.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("msg", getString(R.string.msg_stop_realtime_record))

            val actionDialogFragment: ActionDialogFragment = ActionDialogFragment()
            actionDialogFragment.arguments = bundle

            actionDialogFragment.show(supportFragmentManager, null)

            actionDialogFragment.setMyDialogCallback(object: ActionDialogFragment.MyDialogCallback {
                //중지 텍스트뷰를 클릭하면 -> 액티비티 종료
                override fun finish(isFinished: Boolean) {
                    if (isFinished)
                        finish()
                }

                override fun save(isSaved: Boolean) {
                }

                override fun delete(isDelete: Boolean) {
                }
            })
        }


    }
}