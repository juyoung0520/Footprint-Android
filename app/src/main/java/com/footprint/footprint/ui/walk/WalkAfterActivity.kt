package com.footprint.footprint.ui.walk

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.data.remote.footprint.Footprint
import com.footprint.footprint.data.remote.walk.AcquiredBadge
import com.footprint.footprint.data.remote.walk.WalkInfoResponse
import com.footprint.footprint.data.remote.walk.WalkService
import com.footprint.footprint.databinding.ActivityWalkAfterBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.google.gson.Gson

class WalkAfterActivity :
    BaseActivity<ActivityWalkAfterBinding>(ActivityWalkAfterBinding::inflate), WalkView {
    private lateinit var actionDialogFragment: ActionDialogFragment
    private lateinit var navHostFragment: NavHostFragment

    private var title: String = ""

    override fun initAfterBinding() {
        setMyClickListener()
        setActionDialog()

        navHostFragment = supportFragmentManager.findFragmentById(R.id.walk_after_nav_host_fragment_container) as NavHostFragment
        val navController: NavController = navHostFragment.findNavController()

        //WalkConfirmFragment 에 산책 데이터 전달
        val walk = intent.getStringExtra("walk")
        val bundle = Bundle()
        bundle.putString("walk", walk)
        navController.setGraph(R.navigation.navigation_walk_confirm, bundle)

        val walkModel = Gson().fromJson<WalkModel>(walk, WalkModel::class.java)
        title = walkModel.walkTitle

        binding.walkAfterTitleTv.text = title
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun setMyClickListener() {
        //취소 텍스트뷰 클릭 리스너
        binding.walkAfterCancelTv.setOnClickListener {
            //‘OO번째 산책’ 작성을 취소할까요? 다이얼로그 화면 띄우기
            setWalkDialogBundle("'${title}' 작성을 취소할까요?", getString(R.string.action_delete))
            actionDialogFragment.show(supportFragmentManager, null)
        }

        //저장 텍스트뷰 클릭 리스너
        binding.walkAfterSaveTv.setOnClickListener {
            //‘OO번째 산책’을 저장할까요? 다이얼로그 화면 띄우기
            setWalkDialogBundle("'${title}'을 저장할까요?", getString(R.string.action_save))
            actionDialogFragment.show(supportFragmentManager, null)
        }
    }

    //WalkDialogFragment 초기화
    private fun setActionDialog() {
        actionDialogFragment = ActionDialogFragment()

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {

            override fun action1(isAction: Boolean) {
                if (isAction) {
                    val fragment = (navHostFragment.childFragmentManager.fragments[0]) as WalkConfirmFragment
                    val walkModel = fragment.getWalk()
                    WalkService.writeWalk(this@WalkAfterActivity, walkModel)
                }
            }

            override fun action2(isAction: Boolean) {
            }
        })
    }

    //WalkDialogFragment 에 넘겨준 메세지(ex.‘OO번째 산책’을 저장할까요?)를 저장하는 함수
    private fun setWalkDialogBundle(msg: String, action: String) {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", msg)
        bundle.putString("action", action)

        actionDialogFragment.arguments = bundle
    }

    override fun onWalkLoading() {
        binding.walkAfterLoadingPb.visibility = View.VISIBLE
    }

    override fun onWriteWalkSuccess(badgeList: List<AcquiredBadge>) {
        binding.walkAfterLoadingPb.visibility = View.INVISIBLE

        if (badgeList.isEmpty())
            finish()
        else {

        }
    }

    override fun onGetWalkSuccess(walk: WalkInfoResponse) {
    }

    override fun onGetFootprintsSuccess(footprints: List<Footprint>?) {
    }

    override fun onDeleteWalkSuccess() {
    }

    override fun onWalkFail(code: Int, message: String) {
        binding.walkAfterLoadingPb.visibility = View.INVISIBLE
        showToast(getString(R.string.error_api_fail))
    }
}