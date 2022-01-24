package com.footprint.footprint.ui.main.calendar

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.model.FootprintModel
import com.footprint.footprint.data.model.FootprintsModel
import com.footprint.footprint.databinding.ActivityWalkDetailBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.google.gson.Gson

class WalkDetailActivity :
    BaseActivity<ActivityWalkDetailBinding>(ActivityWalkDetailBinding::inflate) {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var actionDialogFragment: ActionDialogFragment

    //발자국 임시 데이터
    private val footprintList = arrayListOf<FootprintModel>(
        FootprintModel(
            content = "발자국1 #발자국1",
            time = "00:00",
            hashTags = arrayListOf("#발자국1")
        ),
        FootprintModel(
            content = "발자국2 #발자국2",
            time = "00:00",
            hashTags = arrayListOf("#발자국2")
        ),
        FootprintModel(
            content = "발자국3 #발자국3",
            time = "00:00",
            hashTags = arrayListOf("#발자국3")
        ),
        FootprintModel(
            content = "발자국4 #발자국4",
            time = "00:00",
            hashTags = arrayListOf("#발자국4")
        )
    )

    override fun initAfterBinding() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.walk_detail_nav_host_fragment_container) as NavHostFragment
        val navController: NavController = navHostFragment.findNavController()

        //이전 화면에서 얻는 산책 상세 데이터를 WalkMapFragment 에 전달(지금은 임시로 발자국 데이터만)
        val footprints = FootprintsModel(footprintList)
        val bundle = Bundle()
        bundle.putString("footprints", Gson().toJson(footprints))
        navController.setGraph(R.navigation.navigation_walk_confirm, bundle)

        setActionDialog()
        setMyClickListener()
    }

    private fun setMyClickListener() {
        //뒤로가기 이미지뷰 클릭 리스너 -> 액티비티 종료
        binding.walkDetailBackIv.setOnClickListener {
            finish()
        }

        //전체 삭제 텍스트뷰 클릭 리스너 -> 'OO번째 산책' 을 삭제하시겠어요? ActionDialog 띄우기
        binding.walkDetailAllDeleteTv.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("msg", "'OO번째 산책' 을 삭제하시겠어요?")

            actionDialogFragment.arguments = bundle
            actionDialogFragment.show(supportFragmentManager, null)
        }
    }

    //ActionDialogFragment 초기화
    private fun setActionDialog() {
        actionDialogFragment = ActionDialogFragment()
        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun finish(isFinished: Boolean) {
            }

            override fun save(isSaved: Boolean) {
            }

            override fun delete(isDelete: Boolean) {
                if (isDelete) {
                    showToast("전체 삭제 완료")
                    finish()
                }
            }

        })
    }
}