package com.footprint.footprint.ui.walk

import android.os.Bundle
import android.view.View
import androidx.annotation.UiThread
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityWalkDetailBinding
import com.footprint.footprint.domain.model.GetWalkEntity
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.FootprintRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.dialog.FootprintDialogFragment
import com.footprint.footprint.ui.dialog.MsgDialogFragment
import com.footprint.footprint.domain.model.GetFootprintEntity
import com.footprint.footprint.domain.model.SaveWalkFootprintEntity
import com.footprint.footprint.utils.*
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceHeight
import com.footprint.footprint.viewmodel.WalkViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalkDetailActivity :
    BaseActivity<ActivityWalkDetailBinding>(ActivityWalkDetailBinding::inflate),
    OnMapReadyCallback {
    private lateinit var actionDialogFragment: ActionDialogFragment
    private lateinit var footprintDialogFragment: FootprintDialogFragment
    private lateinit var footprintRVAdapter: FootprintRVAdapter
    private lateinit var networkErrSb: Snackbar

    private val walkVm: WalkViewModel by viewModel()
    private val args: WalkDetailActivityArgs by navArgs()

    private var tempUpdateFootprintPosition: Int? = null    //수정하고자 하는 발자국의 RV 어댑터 위치
    private var tempUpdateSaveWalkFootprint: GetFootprintEntity? = null  //수정하고자 하는 발자국 데이터

    private lateinit var paths: MutableList<MutableList<LatLng>> // 지도에 표시할 경로
    private lateinit var footprints: List<List<Double>>

    override fun initAfterBinding() {
        observe()
        setMyClickListener()
        setActionDialog()
        initFootprintDialog()

        binding.walkDetailLoadingPb.visibility = View.VISIBLE
        binding.walkDetailTitleTv.text = "${args.walkIdx}번째 산책"
        walkVm.getWalkByIdx(args.walkIdx)
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
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
            bundle.putString("left", getString(R.string.action_cancel))
            bundle.putString("right", getString(R.string.action_delete))

            actionDialogFragment.arguments = bundle
            actionDialogFragment.show(supportFragmentManager, null)
        }
    }

    //ActionDialogFragment 초기화
    private fun setActionDialog() {
        actionDialogFragment = ActionDialogFragment()
        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun leftAction(action: String) {
            }

            override fun rightAction(action: String) {
                walkVm.deleteWalk(args.walkIdx) //산책 정보 삭제 API 호출
            }
        })
    }

    //발자국 수정 시 호출되는 다이얼로그 프래그먼트 초기화 함수
    private fun initFootprintDialog() {
        footprintDialogFragment = FootprintDialogFragment()

        footprintDialogFragment.setMyDialogCallback(object : FootprintDialogFragment.MyDialogCallback {
            override fun sendFootprint(saveWalkFootprint: SaveWalkFootprintEntity) {
            }

            override fun sendUpdatedFootprint(saveWalkFootprint: SaveWalkFootprintEntity) {
            }

            override fun sendUpdatedFootprint(getFootprintEntity: GetFootprintEntity) {
                //수정된 데이터만 모아서 요청하기
                val reqMap: HashMap<String, Any> = HashMap()

                if (getFootprintEntity.write != tempUpdateSaveWalkFootprint!!.write)   //글
                    reqMap["write"] = getFootprintEntity.write

                if (getFootprintEntity.tagList != tempUpdateSaveWalkFootprint!!.tagList) { //해시태그
                    if (getFootprintEntity.tagList!!.isEmpty()) {    //해시태그를 모두 삭제한 경우
                        reqMap["tagList"] = ""
                    } else {    //해시 태그를 모두 삭제하지 않은 경우
                        for (i in getFootprintEntity.tagList!!.indices) {
                            reqMap["tagList[$i]"] = getFootprintEntity.tagList!![i]
                        }
                    }
                }

                var photos: List<String>? = null    //사진 -> 수정된 사진이 없으면 null
                if (getFootprintEntity.photoList != tempUpdateSaveWalkFootprint!!.photoList) {
                    photos = getFootprintEntity.photoList
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
    private fun bindWalkInfo(getWalkEntity: GetWalkEntity) {
        binding.walkDetailWalkDateTv.text = "${getWalkEntity.walkDate} ${getWalkEntity.walkStartTime}~${getWalkEntity.walkEndTime}"    //산책 날짜
        binding.walkDetailTimeDescTv.text = "${getWalkEntity.walkDate} ${getWalkEntity.walkStartTime}~${getWalkEntity.walkEndTime}"    //산책 날짜(발자국 화면 부분)
        binding.walkDetailWalkTimeTv.text = getWalkEntity.walkTime //산책 시간
        binding.walkDetailCalorieTv.text = getWalkEntity.calorie.toString()  //산책하는 동안 소모된 칼로리
        binding.walkDetailDistanceTv.text = getWalkEntity.distance.toString()    //산책 거리
        binding.walkDetailRecordTv.text = getWalkEntity.footCount.toString() //발자국 횟수

        if (getWalkEntity.footCount==0) {   //남긴 발자국이 없으면 -> 산책기록이 없어요! 화면 보여주기
            binding.walkDetailLoadingPb.visibility = View.INVISIBLE   //로딩 프로그래스바 INVISIBLE

            binding.walkDetailSlidedLayout.visibility = View.INVISIBLE
            binding.walkDetailNoFootprintTv.visibility = View.VISIBLE
        } else //남긴 발자국이 있으면 -> 산책별 발자국 리스트 조회 API 요청
            walkVm.getFootprintsByWalkIdx(args.walkIdx)
    }

    //발자국 리사이클러뷰 어댑터 클래스 초기화
    private fun initAdapter(saveWalkFootprints: ArrayList<GetFootprintEntity>) {
        footprintRVAdapter = FootprintRVAdapter()
        footprintRVAdapter.setMyItemClickListener(object : FootprintRVAdapter.MyItemClickListener {
            override fun addFootprint(position: Int) {
            }

            override fun updateFootprintVerAfter(position: Int, saveWalkFootprint: SaveWalkFootprintEntity) {
            }

            //발자국 편집 텍스트뷰 클릭 리스너
            override fun updateFootprintVerDetail(position: Int, saveWalkFootprint: GetFootprintEntity) {
                //수정하고자 하는 발자국의 어댑터 위치와 데이터를 임시 저장
                tempUpdateFootprintPosition = position
                tempUpdateSaveWalkFootprint = saveWalkFootprint

                val bundle: Bundle = Bundle()
                bundle.putString("footprint", Gson().toJson(saveWalkFootprint))
                bundle.putBoolean("isSaved", true) //이전에 저장됐던 발자국인지 보내주기 -> 저장했던 발자국을 수정할 거니까 true
                footprintDialogFragment.arguments = bundle
                footprintDialogFragment.show(supportFragmentManager, null)
            }
        })

        //어댑터에 데이터 저장
        footprintRVAdapter.setDataDetailVer(saveWalkFootprints)
        binding.walkDetailPostRv.adapter = footprintRVAdapter
    }

    private fun observe() {
        walkVm.mutableErrorType.observe(this, Observer {
            binding.walkDetailLoadingPb.visibility = View.INVISIBLE   //로딩 프로그래스바 INVISIBLE

            when (it) {
                ErrorType.NETWORK -> {
                    when (walkVm.getErrorType()) {
                        "getWalkByIdx" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) { walkVm.getWalkByIdx(args.walkIdx) }
                        "getFootprintsByWalkIdx" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) { walkVm.getFootprintsByWalkIdx(args.walkIdx) }
                        "updateFootprint" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE)
                        "deleteWalk" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) { walkVm.deleteWalk(args.walkIdx) }
                    }

                    networkErrSb.show()
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    startErrorActivity("WalkDetailActivity")
                }
            }
        })

        //산책 기록 observe
        walkVm.getWalkEntity.observe(this, Observer {
            bindWalkInfo(it)    //데이터 바인딩
            paths = it.coordinate
            footprints = it.footCoordinates
            initWalkDetailMap()     // 지도 호출
        })

        walkVm.footprints.observe(this, Observer {
            binding.walkDetailLoadingPb.visibility = View.INVISIBLE   //로딩 프로그래스바 INVISIBLE

            binding.walkDetailSlidingUpPanelLayout.panelHeight = (getDeviceHeight() - convertDpToPx(this@WalkDetailActivity, 90) - (getDeviceHeight() * 0.42)).toInt()
            binding.walkDetailSlidedLayout.visibility = View.VISIBLE
            binding.walkDetailNoFootprintTv.visibility = View.INVISIBLE

            initAdapter(it as ArrayList)
        })

        walkVm.isUpdate.observe(this, Observer {
            binding.walkDetailLoadingPb.visibility = View.INVISIBLE //로딩바 INVISIBLE

            //스낵바 보이고 있으면 닫기
            if (::networkErrSb.isInitialized && networkErrSb.isShown)
                networkErrSb.dismiss()

            footprintDialogFragment.dismiss()   //발자국 수정 다이얼로그 닫기
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

    private fun initWalkDetailMap() {
        val options = NaverMapOptions()
            .compassEnabled(false)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.walk_detail_map_fragment) as MapFragment?
                ?: MapFragment.newInstance(options).also {
                    supportFragmentManager.beginTransaction().add(R.id.walk_detail_map_fragment, it)
                        .commit()
                }

        // 지도 비동기 호출
        mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        moveMapCamera(paths, naverMap)

        drawWalkPath(paths, this, naverMap)

        drawFootprints(footprints, naverMap)
    }
}