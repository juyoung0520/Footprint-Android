package com.footprint.footprint.ui.walk

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.footprint.footprint.R
import com.footprint.footprint.data.model.FootprintModel
import com.footprint.footprint.data.model.WalkModel
import com.footprint.footprint.data.remote.badge.BadgeInfo
import com.footprint.footprint.data.remote.footprint.Footprint
import com.footprint.footprint.data.remote.walk.WalkService
import com.footprint.footprint.databinding.ActivityWalkAfterBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.FootprintRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.dialog.FootprintDialogFragment
import com.footprint.footprint.ui.dialog.MsgDialogFragment
import com.footprint.footprint.ui.dialog.NewBadgeDialogFragment
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceHeight
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WalkAfterActivity :
    BaseActivity<ActivityWalkAfterBinding>(ActivityWalkAfterBinding::inflate), WalkAfterView {
    private lateinit var actionDialogFragment: ActionDialogFragment
    private lateinit var footprintDialogFragment: FootprintDialogFragment
    private lateinit var newBadgeDialogFragment: NewBadgeDialogFragment
    private lateinit var footprintRVAdapter: FootprintRVAdapter
    private lateinit var walk: WalkModel    //WalkMpFragment 로부터 전달 받은 walk 데이터

    private var tempAddFootprintPosition: Int? = null   //발자국을 추가할 때 추가하려고 하는 위치를 임시 저장하는 변수
    private var tempUpdateFootprintPosition: Int? = null    //발자국을 수정할 때 수정하려고 하는 위치를 임시 저장하는 변수

    private val jobs: ArrayList<Job> = arrayListOf()
    private val acquireBadges: ArrayList<BadgeInfo> = arrayListOf() //산책 저장 후 얻은 뱃지 리스트를 저장하는 전역 변수

    override fun initAfterBinding() {
        setMyClickListener()
        setActionDialog()
        initFootprintDialog()
        initNewBadgeDialog()

        //WalkMpFragment 로부터 전달 받은 walk 데이터
        val walkStr = intent.getStringExtra("walk")
        walk = Gson().fromJson<WalkModel>(walkStr, WalkModel::class.java)

        bindWalkData()  //데이터 바인딩
    }

    override fun onBackPressed() {
        binding.walkAfterSlidingUpPanelLayout.apply {
            if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED || panelState == SlidingUpPanelLayout.PanelState.ANCHORED)   //SlidingUpPanelLayout 이 위로 올라가 있으면 아래로 내리기
                panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            else {    //그렇지 않으면 -> ‘OO번째 산책’ 작성을 취소할까요? 다이얼로그 화면 띄우기
                setWalkDialogBundle("'${walk.walkTitle}' 작성을 취소할까요?", getString(R.string.action_delete))
                actionDialogFragment.show(supportFragmentManager, null)
            }
        }
    }

    override fun onDestroy() {
        for (job in jobs) {
            job.cancel()
        }

        super.onDestroy()
    }

    private fun setMyClickListener() {
        //취소 텍스트뷰 클릭 리스너 -> ‘OO번째 산책’ 작성을 취소할까요? 다이얼로그 화면 띄우기
        binding.walkAfterCancelTv.setOnClickListener {
            setWalkDialogBundle("'${walk.walkTitle}' 작성을 취소할까요?", getString(R.string.action_delete))
            actionDialogFragment.show(supportFragmentManager, null)
        }

        //저장 텍스트뷰 클릭 리스너 -> ‘OO번째 산책’을 저장할까요? 다이얼로그 화면 띄우기
        binding.walkAfterSaveTv.setOnClickListener {
            setWalkDialogBundle("'${walk.walkTitle}'을 저장할까요?", getString(R.string.action_save))
            actionDialogFragment.show(supportFragmentManager, null)
        }

        //발자국 추가 텍스트뷰 클릭 리스너 -> FootprintDialogFragment 띄우기
        binding.walkAfterPlusTv.setOnClickListener {
            footprintDialogFragment.show(supportFragmentManager, null)
        }
    }

    //WalkDialogFragment 초기화
    private fun setActionDialog() {
        actionDialogFragment = ActionDialogFragment()

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {

            //‘OO번째 산책’ 작성을 취소할까요?
            override fun action1(isAction: Boolean) {
                if (isAction)   //삭제 버튼 누르면 액티비티 종료
                    finish()
            }

            //‘OO번째 산책’을 저장할까요?
            override fun action2(isAction: Boolean) {
                if (isAction)   //저장 버튼 누르면 산책 정보 저장 API 호출
                    WalkService.writeWalk(this@WalkAfterActivity, walk)
            }
        })
    }

    //FootprintDialogFragment 초기화 함수
    private fun initFootprintDialog() {
        footprintDialogFragment = FootprintDialogFragment()

        footprintDialogFragment.setMyDialogCallback(object : FootprintDialogFragment.MyDialogCallback {
            //발자국 추가
            override fun sendFootprint(footprint: FootprintModel) {
                //발자국을 남겼어요 메세지 다이얼로그 띄우기
                val bundle: Bundle = Bundle()
                bundle.putString("msg", getString(R.string.msg_leave_footprint))
                val msgDialogFragment: MsgDialogFragment = MsgDialogFragment()
                msgDialogFragment.arguments = bundle
                msgDialogFragment.show(supportFragmentManager, null)

                footprint.isMarked = 0  //얘는 산책 중에 남긴 게 아니니까 0으로 변경

                if (tempAddFootprintPosition==null) {   //첫번째 발자국을 남긴 경우: 산책 기록이 없어요! -> 발자국 RV
                   footprintRVAdapter.addData(footprint, 0)   //어댑터에 데이터 추가하여 UI 업데이트
                    binding.walkAfterPostRv.visibility = View.VISIBLE
                    binding.walkAfterPlusLineView.visibility = View.INVISIBLE
                    binding.walkAfterPlusTv.visibility = View.INVISIBLE
                } else
                    footprintRVAdapter.addData(footprint, tempAddFootprintPosition!!)   //어댑터에 데이터 추가하여 UI 업데이트
                binding.walkAfterRecordTv.text = walk.footprints.size.toString()    //기록 수를 보여주는 텍스트뷰도 증가

                initFootprintDialog()   //발자국 남기기 다이얼로그 프래그먼트 초기화
            }

            //발자국 수정
            override fun sendUpdatedFootprint(footprint: FootprintModel) {
                //발자국을 수정했어요 메세지 다이얼로그 띄우기
                val bundle: Bundle = Bundle()
                bundle.putString("msg", getString(R.string.msg_update_footprint))
                val msgDialogFragment: MsgDialogFragment = MsgDialogFragment()
                msgDialogFragment.arguments = bundle
                msgDialogFragment.show(supportFragmentManager, null)

                footprintRVAdapter.updateDataVerAfter(footprint, tempUpdateFootprintPosition!!) //수정된 발자국으로 어댑터 UI 업데이트

                initFootprintDialog()   //발자국 남기기 다이얼로그 프래그먼트 초기화
            }

            override fun cancel() {
            }

        })
    }

    //산책 정보를 바인딩하는 함수
    private fun bindWalkData() {
        binding.walkAfterTitleTv.text = walk.walkTitle   //산책 이름
        Glide.with(this).load(walk.pathImg).into(binding.walkAfterMapIv)    //산책 동선 이미지
        binding.walkAfterWalkTimeTv.text = walk.walkTime    //산책 시간
        binding.walkAfterCalorieTv.text = walk.calorie.toString()   //칼로리
        binding.walkAfterDistanceTv.text = walk.distance.toString() //산책 거리
        binding.walkAfterRecordTv.text = walk.footprints.size.toString()    //발자국 수
        binding.walkAfterTimeDescTv.text = "${walk.startAt.split(" ")[0].replace("-", ".")} ${walk.startAt.split(" ")[1].substring(0, 5)}~${walk.endAt.split(" ")[1].substring(0, 5)}"  //산책 날짜

        binding.walkAfterSlidingUpPanelLayout.panelHeight = (getDeviceHeight() - convertDpToPx(this, 90) - (getDeviceHeight() * 0.42)).toInt()  //SlidingPanelLayout 높이 설정

        if (walk.footprints.isEmpty()) {  //산책 도중 기록을 남기지 않았을 때 -> 산책 기록이 없어요!
            binding.walkAfterPostRv.visibility = View.INVISIBLE
            binding.walkAfterPlusLineView.visibility = View.VISIBLE
            binding.walkAfterPlusTv.visibility = View.VISIBLE
        } else {    //산책 도중 기록을 남겼을 때 -> 발자국 정보들을 보여주는 SlidingPanelLayout
            binding.walkAfterPostRv.visibility = View.VISIBLE
            binding.walkAfterPlusLineView.visibility = View.INVISIBLE
            binding.walkAfterPlusTv.visibility = View.INVISIBLE
        }

        initAdapter()   //어댑터 초기화
    }

    //기록 관련 리사이클러뷰 초기화
    private fun initAdapter() {
        footprintRVAdapter = FootprintRVAdapter()
        footprintRVAdapter.setMyItemClickListener(object : FootprintRVAdapter.MyItemClickListener {
            //발자국 추가 텍스트뷰 클릭 리스너
            override fun addFootprint(position: Int) {
                if (walk.footprints.size>=9) {  //현재 발자국 개수가 9개인 경우 -> "발자국은 최대 9개까지 남길 수 있어요" 메세지 다이얼로그 프래그먼트 띄우기
                    val msgDialogFragment: MsgDialogFragment = MsgDialogFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("msg", getString(R.string.error_post_cnt_exceed))
                    msgDialogFragment.arguments = bundle
                    msgDialogFragment.show(supportFragmentManager, null)
                } else {    //FootprintDialogFragment 띄우기
                    tempAddFootprintPosition = position + 1 //발자국 위치를 추가할 position 데이터 저장
                    footprintDialogFragment.show(supportFragmentManager, null)
                }
            }

            //발자국 편집 텍스트뷰 클릭 리스너
            override fun updateFootprintVerAfter(position: Int, footprint: FootprintModel) {
                tempUpdateFootprintPosition = position  //수정하고자 하는 발자국 데이터의 위치 저장

                //수정할 발자국 데이터와 함께 FootprintDialogFragment 띄우기
                val bundle: Bundle = Bundle()
                bundle.putString("footprint", Gson().toJson(footprint))
                footprintDialogFragment.arguments = bundle
                footprintDialogFragment.show(supportFragmentManager, null)
            }

            override fun updateFootprintVerDetail(position: Int, footprint: Footprint) {
            }
        })

        //어댑터에 데이터 저장
        footprintRVAdapter.setDataAfterVer(walk.footprints)
        binding.walkAfterPostRv.adapter = footprintRVAdapter
    }

    //WalkDialogFragment 에 넘겨준 메세지(ex.‘OO번째 산책’을 저장할까요?)를 저장하는 함수
    private fun setWalkDialogBundle(msg: String, action: String) {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", msg)
        bundle.putString("action", action)

        actionDialogFragment.arguments = bundle
    }

    private fun initNewBadgeDialog() {
        newBadgeDialogFragment = NewBadgeDialogFragment()

        newBadgeDialogFragment.setMyDialogCallback(object : NewBadgeDialogFragment.MyDialogCallback {
            override fun confirm() {
                if (acquireBadges.isEmpty())    //모든 뱃지를 보여주면 액티비티 종료
                    finish()
                else    //다음 뱃지 보여주기
                    showNewBadgeDialog(acquireBadges.removeAt(0))
            }

        })
    }

    //NewBadgeDialog 에 얻은 뱃지를 저장해서 다이얼로그에 저장
    private fun showNewBadgeDialog(badge: BadgeInfo) {
        val bundle = Bundle()
        bundle.putString("badge", Gson().toJson(badge))
        newBadgeDialogFragment.arguments = bundle
        newBadgeDialogFragment.show(supportFragmentManager, null)
    }

    override fun onWalkAfterLoading() {
        if (this!=null) {
            jobs.add(lifecycleScope.launch {
                binding.walkAfterLoadingPb.visibility = View.VISIBLE
            })
        }
    }

    override fun onWalkAfterFail(code: Int?, walk: WalkModel) {
        if (this!=null) {
            jobs.add(lifecycleScope.launch {
                binding.walkAfterLoadingPb.visibility = View.INVISIBLE

                when (code) {
                    6000 -> {   //네트워크 연결 문제
                        Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) {
                            WalkService.writeWalk(this@WalkAfterActivity, walk)
                        }.show()
                    }

                    else -> {   //그 이외 문제
                        Snackbar.make(binding.root, getString(R.string.error_api_fail), Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) {
                            WalkService.writeWalk(this@WalkAfterActivity, walk)
                        }.show()
                    }
                }
            })
        }
    }

    override fun onWriteWalkSuccess(badgeList: List<BadgeInfo>) {
        if (this!=null) {
            jobs.add(lifecycleScope.launch {
                binding.walkAfterLoadingPb.visibility = View.INVISIBLE

                if (badgeList.isEmpty()) {  //얻은 뱃지가 없을 때
                    showToast(getString(R.string.msg_save_walk_success))
                    finish()
                } else {  //얻은 뱃지가 있을 때
                    acquireBadges.addAll(badgeList) //전역 변수에 얻은 뱃지 리스트를 저장
                    showNewBadgeDialog(acquireBadges.removeAt(0))   //NewBadgeDialog 띄우기
                }
            })
        }
    }
}