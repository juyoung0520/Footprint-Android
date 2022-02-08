package com.footprint.footprint.ui.walk

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.footprint.footprint.R
import com.footprint.footprint.data.model.FootprintModel
import com.footprint.footprint.data.remote.footprint.Footprint
import com.footprint.footprint.data.remote.footprint.FootprintService
import com.footprint.footprint.data.remote.walk.WalkInfoResponse
import com.footprint.footprint.data.remote.walk.WalkService
import com.footprint.footprint.databinding.ActivityWalkDetailBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.FootprintRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.dialog.FootprintDialogFragment
import com.footprint.footprint.ui.dialog.MsgDialogFragment
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceHeight
import com.google.gson.Gson
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class WalkDetailActivity :
    BaseActivity<ActivityWalkDetailBinding>(ActivityWalkDetailBinding::inflate), WalkDetailView {
    private lateinit var actionDialogFragment: ActionDialogFragment
    private lateinit var footprintDialogFragment: FootprintDialogFragment
    private lateinit var footprintRVAdapter: FootprintRVAdapter

    private val args: WalkDetailActivityArgs by navArgs()

    private var tempUpdateFootprintPosition: Int? = null
    private var tempUpdateFootprint: Footprint? = null

    override fun initAfterBinding() {
        FootprintService.getFootprints(this, args.walkIdx)
        WalkService.getWalk(this, args.walkIdx)

        setMyClickListener()
        setActionDialog()
        initFootprintDialog()
    }

    override fun onBackPressed() {
        binding.walkDetailSlidingUpPanelLayout.apply {
            if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED || panelState == SlidingUpPanelLayout.PanelState.ANCHORED)   //SlidingUpPanelLayout 이 위로 올라가 있으면 아래로 내리기
                panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            else
                super.onBackPressed()
        }
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
            bundle.putString("desc", "*동선을 제외한 발자국이 모두 삭제되고 \n해당 기록은 복구할 수 없어요")
            bundle.putString("action", getString(R.string.action_delete))

            actionDialogFragment.arguments = bundle
            actionDialogFragment.show(supportFragmentManager, null)
        }
    }

    //ActionDialogFragment 초기화
    private fun setActionDialog() {
        actionDialogFragment = ActionDialogFragment()
        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {

            //'OO번째 산책' 을 삭제하시겠어요? 다이얼로그 프래그먼트 콜백 함수
            override fun action1(isAction: Boolean) {
                if (isAction)
                    WalkService.deleteWalk(this@WalkDetailActivity, args.walkIdx)
            }

            override fun action2(isAction: Boolean) {
            }

        })
    }

    private fun initFootprintDialog() {
        footprintDialogFragment = FootprintDialogFragment()

        footprintDialogFragment.setMyDialogCallback(object : FootprintDialogFragment.MyDialogCallback {
            override fun sendFootprint(footprint: FootprintModel) {
            }

            override fun sendUpdatedFootprint(footprint: FootprintModel) {
                initFootprintDialog()   //발자국 남기기 다이얼로그 프래그먼트 초기화
                Log.d("WalkDetailActivity", "sendUpdatedFootprint tempUpdateFootprint: $tempUpdateFootprint")
                Log.d("WalkDetailActivity", "sendUpdatedFootprint footprint: $footprint")

                //수정된 데이터만 모아서 요청하기
                val reqMap: HashMap<String, Any> = HashMap()
                if (footprint.write!=tempUpdateFootprint!!.write)
                    reqMap["write"] = footprint.write
                if (footprint.hashtagList!=tempUpdateFootprint!!.tagList) {
                    for (i in footprint.hashtagList!!.indices) {
                        reqMap["tagList[$i]"] = footprint.hashtagList!![i]
                    }
                }

                var photos: List<String>? = null
                if (footprint.photos!=tempUpdateFootprint!!.photoList) {
                    photos = if (footprint.photos.isEmpty())
                        null
                    else
                        footprint.photos
                }

                Log.d("WalkDetailActivity", "sendUpdatedFootprint reqMap: $reqMap")

                //발자국 수정 요청 보내기
                FootprintService.updateFootprint(this@WalkDetailActivity, tempUpdateFootprint!!.footprintIdx, reqMap, photos)
            }

        })
    }

    private fun bindWalkInfo(walk: WalkInfoResponse) {
        binding.walkDetailWalkDateTv.text =
            "${walk.getWalkTime.date} ${walk.getWalkTime.startAt}~${walk.getWalkTime.endAt}"
        binding.walkDetailTimeDescTv.text =
            "${walk.getWalkTime.date} ${walk.getWalkTime.startAt}~${walk.getWalkTime.endAt}"
        binding.walkDetailWalkTimeTv.text = walk.getWalkTime.timeString
        binding.walkDetailCalorieTv.text = walk.calorie.toString()
        binding.walkDetailDistanceTv.text = walk.distance.toString()
        binding.walkDetailRecordTv.text = walk.footCount.toString()
        Glide.with(this).load(walk.pathImageUrl).into(binding.walkDetailMapIv)
    }

    //기록 관련 리사이클러뷰 초기화
    private fun initAdapter(footprints: ArrayList<Footprint>) {
        footprintRVAdapter = FootprintRVAdapter()
        footprintRVAdapter.setMyItemClickListener(object : FootprintRVAdapter.MyItemClickListener {
            override fun addFootprint(position: Int) {
            }

            override fun updateFootprintVerAfter(position: Int, footprint: FootprintModel) {
            }

            //발자국 편집 텍스트뷰 클릭 리스너
            override fun updateFootprintVerDetail(position: Int, footprint: Footprint) {
                Log.d("WalkDetailActivity", "updateFootprintVerDetail footprint: $footprint")
                tempUpdateFootprintPosition = position
                tempUpdateFootprint = footprint

                val bundle: Bundle = Bundle()
                bundle.putString("footprint", Gson().toJson(footprint))
                footprintDialogFragment.arguments = bundle
                footprintDialogFragment.show(supportFragmentManager, null)
            }
        })

        //어댑터에 데이터 저장
        footprintRVAdapter.setDataDetailVer(footprints)
        binding.walkDetailPostRv.adapter = footprintRVAdapter
    }

    override fun onWalkDetailLoading() {
        binding.walkDetailLoadingPb.visibility = View.VISIBLE   //로딩 프로그래스바 INVISIBLE
    }

    override fun onWalkDetailFail(code: Int, message: String) {
        binding.walkDetailLoadingPb.visibility = View.INVISIBLE //로딩 프로그래스바 INVISIBLE
        showToast(getString(R.string.error_api_fail))
    }

    override fun onGetWalkSuccess(walk: WalkInfoResponse) {
        Log.d("WalkDetailActivity", "\nonGetWalkSuccess\nwalk: $walk")
        bindWalkInfo(walk)
    }

    override fun onGetFootprintsSuccess(footprints: List<Footprint>?) {
        Log.d("WalkDetailActivity", "\nonGetFootprintsSuccess\nfootprints: $footprints")

        if (footprints == null) {   //발자국이 없는 산책 정보는 "산책 기록이 없어요!" 텍스트뷰 보여주기
            binding.walkDetailSlidedLayout.visibility = View.INVISIBLE
            binding.walkDetailNoFootprintTv.visibility = View.VISIBLE
            binding.walkDetailAllDeleteTv.visibility = View.INVISIBLE
        } else {    //발자국이 있는 산책 정보는 slidedPanelLayout 보여주기
            binding.walkDetailSlidingUpPanelLayout.panelHeight =
                (getDeviceHeight() - convertDpToPx(this, 90) - (getDeviceHeight() * 0.42)).toInt()
            binding.walkDetailSlidedLayout.visibility = View.VISIBLE
            binding.walkDetailNoFootprintTv.visibility = View.INVISIBLE
            binding.walkDetailAllDeleteTv.visibility = View.VISIBLE

            initAdapter(footprints as ArrayList<Footprint>)
        }
    }

    //삭제 요청이 성공적으로 응답하면 산책 정보랑 발자국 정보 다시 받아오기
    override fun onDeleteWalkSuccess() {
        FootprintService.getFootprints(this, args.walkIdx)
        WalkService.getWalk(this, args.walkIdx)
    }

    override fun onUpdateFootprintSuccess() {
        binding.walkDetailLoadingPb.visibility = View.INVISIBLE //로딩 프로그래스바 INVISIBLE

        //발자국을 수정했어요 메세지 다이얼로그 띄우기
        val bundle: Bundle = Bundle()
        bundle.putString("msg", getString(R.string.msg_update_footprint))
        val msgDialogFragment: MsgDialogFragment = MsgDialogFragment()
        msgDialogFragment.arguments = bundle
        msgDialogFragment.show(supportFragmentManager, null)

        FootprintService.getFootprints(this, args.walkIdx)
    }
}