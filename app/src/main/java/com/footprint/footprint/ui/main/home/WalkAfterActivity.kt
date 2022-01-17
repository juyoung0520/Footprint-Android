package com.footprint.footprint.ui.main.home

import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityWalkAfterBinding
import com.footprint.footprint.model.PostModel
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.PostRVAdapter
import com.footprint.footprint.ui.dialog.WalkDialogFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class WalkAfterActivity :
    BaseActivity<ActivityWalkAfterBinding>(ActivityWalkAfterBinding::inflate) {
    override fun initAfterBinding() {
        initAdapter()
        setMyClickListener()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        setSlidingPanelLayoutSize()
    }

    override fun onBackPressed() {
        binding.walkAfterSlidingUpPanelLayout.apply {
            if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED || panelState == SlidingUpPanelLayout.PanelState.ANCHORED)
                panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            else
                super.onBackPressed()
        }
    }

    //SlidingPanelLayout(기록 레이아웃) 높이 설정하는 함수
    private fun setSlidingPanelLayoutSize() {
        binding.walkAfterSlidingUpPanelLayout.panelHeight =
            binding.walkAfterSlidingUpPanelLayout.height - binding.walkAfterTb.height - binding.walkAfterMapIv.height - binding.walkAfterInfoLayout.height
    }

    private fun initAdapter() {
        val posts = arrayListOf(
            PostModel(
                "서울 도심에 대나무 숲이 있기가 쉽지 않은데.. 숨겨진 맛집을 찾은 기분이다. 우리 동네에도 이런 곳이 있었구나. \n" +
                        "조금 언덕이고 길이 좁긴 하지만 사람도 없고 한적해서 도심속에서 벗어나 휴양림에 들어온 기분이었다.\n" +
                        "나중에 엄마 모시고 산책할때 다시 와봐야겠다. 좋아하실것 같아.",
                arrayListOf(R.drawable.ic_temp_post_photo, R.drawable.ic_temp_post_photo),
                "13:40"
            ),
            PostModel(
                "산책중에 예전에 알바했던 가게를 우연히 지나쳤다. 일은 힘들어도 사장님은 참 잘해주셨었던 가게 였는데. 옛날 일할때 생각도 나면서 사장님 아직",
                arrayListOf(),
                "13:42"
            ),
            PostModel(
                "여기도 경치 맛집이지. 주변 산들이 탁트이게 보여서 시원하고 날씨도 되게 좋았고. 항상 여기",
                arrayListOf(R.drawable.ic_temp_post_photo),
                "13:50"
            )
        )

        val postRVAdapter = PostRVAdapter()
        postRVAdapter.setMyItemClickListener(object : PostRVAdapter.MyItemClickListener {
            override fun showDeleteDialog() {
                WalkDialogFragment(getString(R.string.msg_delete_post)).show(
                    supportFragmentManager,
                    null
                )
            }
        })

        postRVAdapter.setData(posts)
        binding.walkAfterPostRv.adapter = postRVAdapter
    }

    private fun setMyClickListener() {
        binding.walkAfterCancelTv.setOnClickListener {
            WalkDialogFragment(getString(R.string.msg_cancel_footprint)).show(
                supportFragmentManager,
                null
            )
        }

        binding.walkAfterSaveTv.setOnClickListener {
            WalkDialogFragment(getString(R.string.msg_save_footprint)).show(
                supportFragmentManager,
                null
            )
        }
    }
}