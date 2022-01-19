package com.footprint.footprint.ui.main.home

import android.os.Bundle
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityWalkAfterBinding
import com.footprint.footprint.model.PostsModel
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.PostRVAdapter
import com.footprint.footprint.ui.dialog.WalkDialogFragment
import com.google.gson.Gson
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class WalkAfterActivity :
    BaseActivity<ActivityWalkAfterBinding>(ActivityWalkAfterBinding::inflate) {
    private lateinit var walkDialogFragment: WalkDialogFragment
    private lateinit var posts: PostsModel
    private lateinit var postRVAdapter: PostRVAdapter

    private var position: Int = -1  //클릭된 기록 인덱스

    override fun initAfterBinding() {
        initAdapter()
        setMyClickListener()
        setWalkDialog()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        setSlidingPanelLayoutSize()
    }

    override fun onBackPressed() {
        binding.walkAfterSlidingUpPanelLayout.apply {
            //SlidingUpPanelLayout 이 위로 올라가 있으면 아래로 내리기
            if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED || panelState == SlidingUpPanelLayout.PanelState.ANCHORED)
                panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            else {
                //‘OO번째 산책’ 작성을 취소할까요? 다이얼로그 화면 띄우기
                setWalkDialogBundle(getString(R.string.msg_stop_walk))
                walkDialogFragment.show(
                    supportFragmentManager,
                    null
                )
            }
        }
    }

    //SlidingPanelLayout(기록 레이아웃) 높이 설정하는 함수
    private fun setSlidingPanelLayoutSize() {
        binding.walkAfterSlidingUpPanelLayout.panelHeight =
            binding.walkAfterSlidingUpPanelLayout.height - binding.walkAfterTb.height - binding.walkAfterMapIv.height - binding.walkAfterInfoLayout.height
    }

    //기록 관련 리사이클러뷰 초기화
    private fun initAdapter() {
        postRVAdapter = PostRVAdapter()
        postRVAdapter.setMyItemClickListener(object : PostRVAdapter.MyItemClickListener {
            //기록 삭제 텍스트뷰 클릭 리스너 -> 해당 발자국을 삭제할까요? 다이얼로그 화면 띄우기
            override fun showDeleteDialog(position: Int) {
                this@WalkAfterActivity.position = position  //클릭된 post 인덱스를 전역변수로 저장해 놓는다.
                setWalkDialogBundle(getString(R.string.msg_delete_footprint))
                walkDialogFragment.show(supportFragmentManager, null)
            }
        })

        //이전 화면(WalkMapFragment)에서 전달받은 기록 데이터(posts)로 어댑터에 데이터 저장
        val postsStr = intent.getStringExtra("posts")
        if (postsStr != null) {
            posts = Gson().fromJson(postsStr, PostsModel::class.java)
            postRVAdapter.setData(posts)
        }
        binding.walkAfterPostRv.adapter = postRVAdapter
    }

    private fun setMyClickListener() {
        //취소 텍스트뷰 클릭 리스너
        binding.walkAfterCancelTv.setOnClickListener {
            //‘OO번째 산책’ 작성을 취소할까요? 다이얼로그 화면 띄우기
            setWalkDialogBundle(getString(R.string.msg_stop_walk))
            walkDialogFragment.show(
                supportFragmentManager,
                null
            )
        }

        //저장 텍스트뷰 클릭 리스너
        binding.walkAfterSaveTv.setOnClickListener {
            //‘OO번째 산책’을 저장할까요? 다이얼로그 화면 띄우기
            setWalkDialogBundle(getString(R.string.msg_save_walk))
            walkDialogFragment.show(
                supportFragmentManager,
                null
            )
        }
    }

    //WalkDialogFragment 초기화
    private fun setWalkDialog() {
        walkDialogFragment = WalkDialogFragment()

        walkDialogFragment.setMyDialogCallback(object : WalkDialogFragment.MyDialogCallback {
            override fun finish(isFinished: Boolean) {
                if (isFinished)
                    finish()
            }

            override fun save(isSaved: Boolean) {
                if (isSaved)
                    finish()
            }

            override fun delete(isDelete: Boolean) {
                if (isDelete)
                    postRVAdapter.removeData(this@WalkAfterActivity.position)
            }
        })
    }

    //WalkDialogFragment 에 넘겨준 메세지(ex.‘OO번째 산책’을 저장할까요?)를 저장하는 함수
    private fun setWalkDialogBundle(msg: String) {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", msg)

        walkDialogFragment.arguments = bundle
    }
}