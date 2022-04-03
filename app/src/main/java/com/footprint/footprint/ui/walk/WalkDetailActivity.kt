package com.footprint.footprint.ui.walk

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityWalkDetailBinding
import com.footprint.footprint.domain.model.Footprint
import com.footprint.footprint.domain.model.Walk
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.FootprintRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.dialog.FootprintDialogFragment
import com.footprint.footprint.ui.dialog.MsgDialogFragment
import com.footprint.footprint.ui.walk.model.FootprintUIModel
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceHeight
import com.footprint.footprint.viewmodel.WalkViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalkDetailActivity :
    BaseActivity<ActivityWalkDetailBinding>(ActivityWalkDetailBinding::inflate) {
    private lateinit var actionDialogFragment: ActionDialogFragment
    private lateinit var footprintDialogFragment: FootprintDialogFragment
    private lateinit var footprintRVAdapter: FootprintRVAdapter

    private val walkVm: WalkViewModel by viewModel()
    private val args: WalkDetailActivityArgs by navArgs()

    private var tempUpdateFootprintPosition: Int? = null    //수정하고자 하는 발자국의 RV 어댑터 위치
    private var tempUpdateFootprint: Footprint? = null  //수정하고자 하는 발자국 데이터

    override fun initAfterBinding() {
        observe()
        setMyClickListener()
        setActionDialog()
        initFootprintDialog()

        walkVm.getWalkByIdx(args.walkIdx)
        binding.walkDetailTitleTv.text = "${args.walkIdx}번째 산책"

        binding.walkDetailTitleTv.text = "${args.walkIdx}번째 산책"
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
            bundle.putString("msg", "'${args.walkIdx}번째 산책' 을 삭제하시겠어요?")
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
                if (isAction)   //사용자가 삭제 버튼을 누른 경우
                    walkVm.deleteWalk(args.walkIdx) //산책 정보 삭제 API 호출
            }

            override fun action2(isAction: Boolean) {
            }

        })
    }

    //발자국 수정 시 호출되는 다이얼로그 프래그먼트 초기화 함수
    private fun initFootprintDialog() {
        footprintDialogFragment = FootprintDialogFragment()

        footprintDialogFragment.setMyDialogCallback(object : FootprintDialogFragment.MyDialogCallback {
            override fun sendFootprint(footprint: FootprintUIModel) {
            }

            override fun sendUpdatedFootprint(footprint: FootprintUIModel) {
                //수정된 데이터만 모아서 요청하기
                val reqMap: HashMap<String, Any> = HashMap()
                if (footprint.write != tempUpdateFootprint!!.write)   //글
                    reqMap["write"] = footprint.write
                if (footprint.hashtagList != tempUpdateFootprint!!.tagList) { //해시태그
                    if (footprint.hashtagList!!.isEmpty()) {    //해시태그를 모두 삭제한 경우
                        reqMap["tagList"] = ""
                    } else {    //해시 태그를 모두 삭제하지 않은 경우
                        for (i in footprint.hashtagList!!.indices) {
                            reqMap["tagList[$i]"] = footprint.hashtagList!![i]
                        }
                    }
                }
                var photos: List<String>? = null    //사진 -> 수정된 사진이 없으면 null
                if (footprint.photos != tempUpdateFootprint!!.photoList) {
                    photos = footprint.photos
                }

                if (reqMap.isEmpty() && photos==null) //변경된 내용 없음 -> "변경된 내용이 없어요" 다이얼로그 띄우기
                    showToast(getString(R.string.error_no_updating_content))
                else {    //발자국 수정 요청 API 호출
                    binding.walkDetailLoadingPb.visibility = View.VISIBLE
                    walkVm.updateFootprint(args.walkIdx, tempUpdateFootprintPosition!! + 1, reqMap, photos)
                }
            }

            override fun cancel() {
                initFootprintDialog()   //다이얼로그 프래그먼트 초기화
            }
        })
    }

    //산책 정보 데이터 바인딩 함수
    private fun bindWalkInfo(walk: Walk) {
        binding.walkDetailWalkDateTv.text = "${walk.getWalkTime.date} ${walk.getWalkTime.startAt}~${walk.getWalkTime.endAt}"    //산책 날짜
        binding.walkDetailTimeDescTv.text = "${walk.getWalkTime.date} ${walk.getWalkTime.startAt}~${walk.getWalkTime.endAt}"    //산책 날짜
        binding.walkDetailWalkTimeTv.text = walk.getWalkTime.timeString //산책 시간
        binding.walkDetailCalorieTv.text = walk.calorie.toString()  //산책하는 동안 소모된 칼로리
        binding.walkDetailDistanceTv.text = walk.distance.toString()    //산책 거리
        binding.walkDetailRecordTv.text = walk.footCount.toString() //발자국 횟수
        Glide.with(this).load(walk.pathImageUrl).into(binding.walkDetailMapIv)  //산책 동선 이미지
    }

    //발자국 리사이클러뷰 어댑터 클래스 초기화
    private fun initAdapter(footprints: ArrayList<Footprint>) {
        footprintRVAdapter = FootprintRVAdapter()
        footprintRVAdapter.setMyItemClickListener(object : FootprintRVAdapter.MyItemClickListener {
            override fun addFootprint(position: Int) {
            }

            override fun updateFootprintVerAfter(position: Int, footprint: FootprintUIModel) {
            }

            //발자국 편집 텍스트뷰 클릭 리스너
            override fun updateFootprintVerDetail(position: Int, footprint: Footprint) {
                //수정하고자 하는 발자국의 어댑터 위치와 데이터를 임시 저장
                tempUpdateFootprintPosition = position
                tempUpdateFootprint = footprint

                //발자국 데이터와 함께 FootprintDialogFragment 호출 -> FootprintDialogFragment 에 보내려면 Footprint -> FootprintModel 로 형 변환 필요
                val footprintModel = FootprintUIModel(
                    recordAt = footprint.recordAt,
                    write = footprint.write,
                    hashtagList = footprint.tagList,
                    photos = footprint.photoList,
                    isMarked = footprint.onWalk
                )

                val bundle: Bundle = Bundle()
                bundle.putString("footprint", Gson().toJson(footprintModel))
                footprintDialogFragment.arguments = bundle
                footprintDialogFragment.show(supportFragmentManager, null)
            }
        })

        //어댑터에 데이터 저장
        footprintRVAdapter.setDataDetailVer(footprints)
        binding.walkDetailPostRv.adapter = footprintRVAdapter
    }

    private fun observe() {
        walkVm.mutableErrorType.observe(this, Observer {
            binding.walkDetailLoadingPb.visibility = View.INVISIBLE   //로딩 프로그래스바 INVISIBLE

            when (it) {
                ErrorType.NETWORK -> {
                    when (walkVm.getErrorType()) {
                        "getWalkByIdx" -> Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_LONG).setAction(getString(R.string.action_retry)) {
                            walkVm.getWalkByIdx(args.walkIdx)
                        }.show()
                        "getFootprintsByWalkIdx" -> Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_LONG).setAction(getString(R.string.action_retry)) {
                            walkVm.getFootprintsByWalkIdx(args.walkIdx)
                        }.show()
                        "updateFootprint" -> Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_LONG).show()
                        "deleteWalk" -> Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_LONG).setAction(getString(R.string.action_retry)) {
                            walkVm.deleteWalk(args.walkIdx)
                        }.show()
                    }

                }
                else -> {
                    when (walkVm.getErrorType()) {
                        "getWalkByIdx" -> Snackbar.make(binding.root, getString(R.string.error_api_fail), Snackbar.LENGTH_LONG).setAction(getString(R.string.action_retry)) {
                            walkVm.getWalkByIdx(args.walkIdx)
                        }.show()
                        "getFootprintsByWalkIdx" -> Snackbar.make(binding.root, getString(R.string.error_api_fail), Snackbar.LENGTH_LONG).setAction(getString(R.string.action_retry)) {
                            walkVm.getFootprintsByWalkIdx(args.walkIdx)
                        }.show()
                        "updateFootprint" -> Snackbar.make(binding.root, getString(R.string.error_api_fail), Snackbar.LENGTH_LONG).show()
                        "deleteWalk" -> Snackbar.make(binding.root, getString(R.string.error_api_fail), Snackbar.LENGTH_LONG).setAction(getString(R.string.action_retry)) {
                            walkVm.deleteWalk(args.walkIdx)
                        }.show()
                    }
                }
            }
        })

        walkVm.walk.observe(this, Observer {
            binding.walkDetailLoadingPb.visibility = View.INVISIBLE   //로딩 프로그래스바 INVISIBLE

            bindWalkInfo(it)

            if (it.footCount==0) {
                binding.walkDetailSlidedLayout.visibility = View.INVISIBLE
                binding.walkDetailNoFootprintTv.visibility = View.VISIBLE
            } else
                walkVm.getFootprintsByWalkIdx(args.walkIdx)   //산책별 발자국 리스트 조회 API 요청
        })

        walkVm.footprints.observe(this, Observer {
            binding.walkDetailSlidingUpPanelLayout.panelHeight =
                (getDeviceHeight() - convertDpToPx(this@WalkDetailActivity, 90) - (getDeviceHeight() * 0.42)).toInt()
            binding.walkDetailSlidedLayout.visibility = View.VISIBLE
            binding.walkDetailNoFootprintTv.visibility = View.INVISIBLE

            initAdapter(it as ArrayList<Footprint>)
        })

        walkVm.isUpdate.observe(this, Observer {
            binding.walkDetailLoadingPb.visibility = View.INVISIBLE
            footprintDialogFragment.dismiss()
            initFootprintDialog()   //다이얼로그 프래그먼트 초기화

            //발자국을 수정했어요 메세지 다이얼로그 띄우기
            val bundle: Bundle = Bundle()
            bundle.putString("msg", getString(R.string.msg_update_footprint))
            val msgDialogFragment: MsgDialogFragment = MsgDialogFragment()
            msgDialogFragment.arguments = bundle
            msgDialogFragment.show(supportFragmentManager, null)

            walkVm.getFootprintsByWalkIdx(args.walkIdx)
        })

        walkVm.isDelete.observe(this, Observer {
            finish()
        })
    }
}