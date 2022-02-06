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
import com.footprint.footprint.data.remote.walk.AcquiredBadge
import com.footprint.footprint.data.remote.walk.WalkInfoResponse
import com.footprint.footprint.data.remote.walk.WalkService
import com.footprint.footprint.databinding.ActivityWalkDetailBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.FootprintRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceHeight
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class WalkDetailActivity :
    BaseActivity<ActivityWalkDetailBinding>(ActivityWalkDetailBinding::inflate), WalkView {
    private lateinit var actionDialogFragment: ActionDialogFragment

    private val args: WalkDetailActivityArgs by navArgs()

    override fun initAfterBinding() {
        FootprintService.getFootprints(this, args.walkIdx)
        WalkService.getWalk(this, args.walkIdx)

        setMyClickListener()
        setActionDialog()
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

            override fun action1(isAction: Boolean) {
                if (isAction) {
                    WalkService.deleteWalk(this@WalkDetailActivity, args.walkIdx)
                }
            }

            override fun action2(isAction: Boolean) {
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
        val footprintRVAdapter = FootprintRVAdapter()
        footprintRVAdapter.setMyItemClickListener(object : FootprintRVAdapter.MyItemClickListener {
            //발자국 추가 텍스트뷰 클릭 리스너
            override fun addFootprint(position: Int) {
            }

            //발자국 편집 텍스트뷰 클릭 리스너
            override fun updateFootprint(position: Int, footprint: FootprintModel) {
                /*this@WalkConfirmFragment.position = position

                //발자국 작성하기 다이얼로그 화면 띄우기(수정)
                val action = WalkConfirmFragmentDirections.actionWalkConfirmFragment2ToFootprintDialogFragment3(Gson().toJson(footprint))
                findNavController().navigate(action)*/
            }
        })

        //어댑터에 데이터 저장
        footprintRVAdapter.setDataDetailVer(footprints)
        binding.walkDetailPostRv.adapter = footprintRVAdapter
    }

    override fun onWalkLoading() {
    }

    override fun onWalkFail(code: Int, message: String) {
        showToast(getString(R.string.error_api_fail))
    }

    override fun onWriteWalkSuccess(badgeList: List<AcquiredBadge>) {
    }

    override fun onGetWalkSuccess(walk: WalkInfoResponse) {
        Log.d("WalkDetailActivity", "\nonGetWalkSuccess\nwalk: $walk")

        bindWalkInfo(walk)
    }

    override fun onGetFootprintsSuccess(footprints: List<Footprint>?) {
        Log.d("WalkDetailActivity", "\nonGetFootprintsSuccess\nfootprints: $footprints")

        if (footprints == null) {
            binding.walkDetailSlidedLayout.visibility = View.INVISIBLE
            binding.walkDetailNoFootprintTv.visibility = View.VISIBLE
            binding.walkDetailAllDeleteTv.visibility = View.INVISIBLE
        } else {
            binding.walkDetailSlidingUpPanelLayout.panelHeight =
                (getDeviceHeight() - convertDpToPx(this, 90) - (getDeviceHeight() * 0.42)).toInt()
            binding.walkDetailSlidedLayout.visibility = View.VISIBLE
            binding.walkDetailNoFootprintTv.visibility = View.INVISIBLE
            binding.walkDetailAllDeleteTv.visibility = View.VISIBLE

            initAdapter(footprints as ArrayList<Footprint>)
        }
    }

    override fun onDeleteWalkSuccess() {
        FootprintService.getFootprints(this, args.walkIdx)
        WalkService.getWalk(this, args.walkIdx)
    }
}