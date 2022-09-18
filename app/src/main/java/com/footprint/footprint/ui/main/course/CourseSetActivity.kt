package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.UiThread
import androidx.lifecycle.Observer
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityCourseSetBinding
import com.footprint.footprint.domain.model.RecommendEntity
import com.footprint.footprint.domain.model.WalkDetailCEntity
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.main.course.model.CourseSetModel
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.CourseSetViewModel
import com.google.android.material.snackbar.Snackbar
import com.jaygoo.widget.OnRangeChangedListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseSetActivity : BaseActivity<ActivityCourseSetBinding>(ActivityCourseSetBinding::inflate),
    OnMapReadyCallback {
    private val vm: CourseSetViewModel by viewModel()
    private val multipartPaths: MutableList<MultipartPathOverlay> = mutableListOf()
    private val courseSetModels: MutableList<CourseSetModel> = mutableListOf()

    private var walkNumber: Int = -1
    private var selectingPathIndex: Int = 0

    private lateinit var startMarker: Marker
    private lateinit var endMarker: Marker
    private lateinit var map: NaverMap
    private lateinit var actionFrag: ActionDialogFragment
    private lateinit var clickedCourseIndex: MutableList<Int>
    private lateinit var walkDetailCEntity: WalkDetailCEntity
    private lateinit var networkErrSb: Snackbar

    override fun initAfterBinding() {
        initWalkAfterMap()
        setMyEventListener()
        initActionFrag()
        observe()
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }

    //뒤로가기 버튼 누르면 공유를 취소할건지 물어보는 action 다이얼로그 화면 띄우기
    override fun onBackPressed() {
        actionFrag.show(supportFragmentManager, null)
    }

    //네이버 지도 세팅
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap

        //RangeSeekbar 설정
        binding.courseSetRsb.leftSeekBar.thumbDrawableId = R.drawable.ic_start_marker
        binding.courseSetRsb.rightSeekBar.thumbDrawableId = R.drawable.ic_end_marker

        //전달 받은 walkNumber 로 산책 코스 저장을 위해 필요한 데이터 조회 API 호출
        walkNumber = intent.getIntExtra("walkNumber", -1)
        vm.getWalkDetailCEntity(walkNumber)
    }

    //CoureSetActivity 에서만 사용되는 데이터 모델 정의하기
    private fun initCourseSetModels() {
        if (walkDetailCEntity.coordinates.size == 1) {   //단일 경로일 때
            val courseSetModel: CourseSetModel = CourseSetModel(selectedCoords = walkDetailCEntity.coordinates[0], isSelected = true)
            courseSetModels.add(courseSetModel)
        } else {    //여러 구간으로 나눠진 코스일 때
            for ((index, path) in walkDetailCEntity.coordinates.withIndex()) {
                var courseSetModel: CourseSetModel? = null

                courseSetModel = if (index == 0) {
                    CourseSetModel(selectedCoords = path, isSelected = true)
                } else {
                    CourseSetModel(selectedCoords = path)
                }

                courseSetModels.add(courseSetModel)
            }
        }
    }

    private fun initWalkAfterMap() {
        val options = NaverMapOptions().compassEnabled(false)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.course_set_map_fragment) as MapFragment?
                ?: MapFragment.newInstance(options).also {
                    supportFragmentManager.beginTransaction().add(R.id.course_set_map_fragment, it)
                        .commit()
                }

        // 지도 비동기 호출
        mapFragment.getMapAsync(this)
    }

    private fun getSinglePathOverlay(path: MutableList<MutableList<LatLng>>, map: NaverMap) {
        //지도에 경로 그리기
        val multipartPath = MultipartPathOverlay()
        multipartPath.width = 30
        multipartPath.outlineWidth = 10
        multipartPath.coordParts = path
        multipartPath.colorParts = listOf(
            MultipartPathOverlay.ColorPart(
                getColor(R.color.primary),
                Color.WHITE,
                getColor(R.color.primary),
                getColor(R.color.primary)
            )
        )
        multipartPath.map = map
        multipartPaths.add(multipartPath)

        //시작점, 끝점 표시하기
        startMarker =
            getMarker(path[0][0], OverlayImage.fromResource(R.drawable.ic_donut_secondary))
        startMarker.map = map
        endMarker = getMarker(
            path[0][path[0].lastIndex],
            OverlayImage.fromResource(R.drawable.ic_donut_primary)
        )
        endMarker.map = map

        //시작과 끝점을 조절하는 RangeSeekBar 를 초기화
        changeRangeBar(0f, 100f)
        //초기화 버튼 INVISIBLE
        binding.courseSetResetBtn.visibility = View.INVISIBLE
    }

    private fun getMultiPathOverlay(paths: MutableList<MutableList<LatLng>>, map: NaverMap) {
        clickedCourseIndex = mutableListOf(0)

        for ((index, path) in paths.withIndex()) {
            val multipartPath = MultipartPathOverlay()
            multipartPath.width = 30
            multipartPath.outlineWidth = 10
            multipartPath.coordParts = listOf(path)

            //출발점, 도착점 아이콘 표시
            when (index) {
                0 -> {
                    multipartPath.colorParts = listOf(
                        MultipartPathOverlay.ColorPart(
                            getColor(R.color.primary),
                            Color.WHITE,
                            getColor(R.color.primary),
                            getColor(R.color.primary)
                        )
                    )
                    startMarker =
                        getMarker(path[0], OverlayImage.fromResource(R.drawable.ic_donut_secondary))
                    startMarker.map = map
                }
                paths.lastIndex -> {
                    multipartPath.colorParts = listOf(
                        MultipartPathOverlay.ColorPart(
                            getColor(R.color.black_light),
                            Color.WHITE,
                            getColor(R.color.black_light),
                            getColor(R.color.black_light)
                        )
                    )
                    endMarker = getMarker(
                        path[path.lastIndex],
                        OverlayImage.fromResource(R.drawable.ic_donut_primary)
                    )
                    endMarker.map = map
                }
                else -> {
                    multipartPath.colorParts = listOf(
                        MultipartPathOverlay.ColorPart(
                            getColor(R.color.black_light),
                            Color.WHITE,
                            getColor(R.color.black_light),
                            getColor(R.color.black_light)
                        )
                    )
                }
            }

            multipartPath.setOnClickListener {
                binding.courseSetResetBtn.visibility = View.VISIBLE //초기화 버튼 VISIBLE

                if (courseSetModels[index].isSelected) {    //원래 선택돼 있던 구간
                    if (selectingPathIndex == index) {    //선택돼 있던 구간이랑 지금 선택한 구간이랑 같을 때 -> 지금 선택한 구간 해제
                        //지금 선택한 구간을 해제한다.
                        multipartPath.coordParts = listOf(walkDetailCEntity.coordinates[index])
                        multipartPath.colorParts = listOf(
                            MultipartPathOverlay.ColorPart(
                                getColor(R.color.black_light),
                                Color.WHITE,
                                getColor(R.color.black_light),
                                getColor(R.color.black_light)
                            )
                        )

                        //지금 선택한 구간의 RangeSeekbar 를 초기화한다.
                        courseSetModels[index].selectedCoords = walkDetailCEntity.coordinates[index]
                        courseSetModels[index].leftValue = 0f
                        courseSetModels[index].rightValue = 100f

                        clickedCourseIndex.remove(index)  //지금 선택한 인덱스를 스택에서 지운다.

                        if (clickedCourseIndex.isEmpty()) { //스택이 비어있으면 -> 디폴트 화면으로 변경
                            selectingPathIndex = 0  //첫번째 구간으로 변경
                            clickedCourseIndex.add(0)   //스택에 첫번재 구간 추가
                            //선택된 구간의 RangeBar UI로 변경 -> 이 부분이 변경되면 알아서 코스 구간도 바뀜.
                            changeRangeBar(
                                courseSetModels[selectingPathIndex].leftValue,
                                courseSetModels[selectingPathIndex].rightValue
                            )
                            binding.courseSetResetBtn.visibility = View.INVISIBLE   //초기화 버튼 INVISIBLE
                        } else {    //스택이 비어있지 않으면 -> 가장 마지막으로 선택했던 구간의 UI 로 이동.
                            selectingPathIndex = clickedCourseIndex.last()  //스택의 가장 마지막(=가장 최근에 선택했다는 뜻) 구간으로 변경
                            //선택된 구간의 RangeBar UI로 변경 -> 이 부분이 변경되면 알아서 코스 구간도 바뀜.
                            changeRangeBar(
                                courseSetModels[selectingPathIndex].leftValue,
                                courseSetModels[selectingPathIndex].rightValue
                            )
                        }
                    } else {    //선택돼 있던 구간이랑 지금 선택한 구간이랑 같지 않을 때 -> 지금 선택한 구간으로 이동
                        selectingPathIndex = index  //현재 선택된 구간의 인덱스로 변경
                        //선택된 구간의 RangeBar UI로 변경 -> 이 부분이 변경되면 알아서 코스 구간도 바뀜.
                        changeRangeBar(
                            courseSetModels[selectingPathIndex].leftValue,
                            courseSetModels[selectingPathIndex].rightValue
                        )
                        clickedCourseIndex.remove(index)  //지금 선택한 인덱스를 스택에서 지우고
                        clickedCourseIndex.add(index)  //지금 선택한 인덱스를 맨 마지막 위치로 보내기
                    }
                } else {    //원래 선택돼 있지 않던 구간
                    selectingPathIndex = index  //현재 선택된 구간의 인덱스로 변경
                    courseSetModels[index].isSelected =
                        !courseSetModels[index].isSelected  //isSelected = true
                    changeRangeBar(
                        courseSetModels[selectingPathIndex].leftValue,
                        courseSetModels[selectingPathIndex].rightValue
                    )   //선택된 구간의 RangeBar UI로 변경 -> 이 부분이 변경되면 알아서 코스 구간도 바뀜.
                    clickedCourseIndex.add(index)   //클릭된 코스 stack 쌓기
                }

                //현재 선택된 코스 구간으로 카메라 움직이기
                map.moveCamera(
                    CameraUpdate.fitBounds(
                        LatLngBounds.from(walkDetailCEntity.coordinates[selectingPathIndex]),
                        200
                    )
                )

                binding.courseSetSectionTv.text = "${selectingPathIndex + 1}/${walkDetailCEntity.coordinates.size}구간"   //몇구간인지 보여주기

                return@setOnClickListener true
            }

            multipartPath.map = map
            multipartPaths.add(multipartPath)
        }

        //첫번째 구간의 시작과 끝점을 조절하는 RangeSeekBar 를 초기화
        changeRangeBar(
            courseSetModels[0].leftValue,
            courseSetModels[0].rightValue
        )
        binding.courseSetResetBtn.visibility = View.INVISIBLE   //초기화 버튼 INVISIBLE
    }

    private fun setMyEventListener() {
        binding.courseSetRsb.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: com.jaygoo.widget.RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                var minMapIdx = (walkDetailCEntity.coordinates[selectingPathIndex].size * leftValue / 100 - 1).toInt()
                var maxMapIdx = (walkDetailCEntity.coordinates[selectingPathIndex].size * rightValue / 100 - 1).toInt()
                if (minMapIdx < 0)
                    minMapIdx = 0
                else if (minMapIdx == walkDetailCEntity.coordinates[selectingPathIndex].size)
                    minMapIdx--
                if (maxMapIdx == walkDetailCEntity.coordinates[selectingPathIndex].size)
                    maxMapIdx--
                else if (maxMapIdx < 0)
                    maxMapIdx = 0

                //아래 RangeSeekbar 조절하기
                binding.courseSetRsbSub.setMinThumbValue(leftValue.toInt())
                binding.courseSetRsbSub.setMaxThumbValue(rightValue.toInt())

                courseSetModels[selectingPathIndex].leftValue = leftValue
                courseSetModels[selectingPathIndex].rightValue = rightValue
                courseSetModels[selectingPathIndex].selectedCoords = walkDetailCEntity.coordinates[selectingPathIndex].subList(minMapIdx, maxMapIdx)

                if (minMapIdx == 0 && maxMapIdx == walkDetailCEntity.coordinates[selectingPathIndex].lastIndex) {  //시작==0, 끝==100 일 때 -> 전체 선택으로 표시
                    binding.courseSetNextTv.isEnabled = true    //다음 버튼 활성화
                    multipartPaths[selectingPathIndex].coordParts =
                        listOf(walkDetailCEntity.coordinates[selectingPathIndex])
                    multipartPaths[selectingPathIndex].colorParts = listOf(
                        MultipartPathOverlay.ColorPart(
                            getColor(R.color.primary),
                            Color.WHITE,
                            getColor(R.color.primary),
                            getColor(R.color.primary)
                        )
                    )
                } else if (minMapIdx == maxMapIdx) {  //시작이랑 끝점이 같을 때 -> 이때는 다음 버튼을 비활성화 시켜서 다음 화면으로 안 넘어가도록 하기
                    binding.courseSetNextTv.isEnabled = false   //다음 버튼 비활성화
                } else {
                    binding.courseSetNextTv.isEnabled = true    //다음 버튼 활성화
                    if (minMapIdx == 0) { //끝점만 움직였을 때
                        multipartPaths[selectingPathIndex].coordParts = listOf(
                            walkDetailCEntity.coordinates[selectingPathIndex].slice(0..maxMapIdx),
                            walkDetailCEntity.coordinates[selectingPathIndex].slice(maxMapIdx until walkDetailCEntity.coordinates[selectingPathIndex].size),
                        )
                        multipartPaths[selectingPathIndex].colorParts = listOf(
                            MultipartPathOverlay.ColorPart(
                                getColor(R.color.primary),
                                Color.WHITE,
                                getColor(R.color.primary),
                                getColor(R.color.primary)
                            ),
                            MultipartPathOverlay.ColorPart(
                                getColor(R.color.black_light),
                                Color.WHITE,
                                getColor(R.color.black_light),
                                getColor(R.color.black_light)
                            )
                        )
                    } else if (maxMapIdx == walkDetailCEntity.coordinates[selectingPathIndex].lastIndex) {   //시작점만 움직였을 때
                        multipartPaths[selectingPathIndex].coordParts = listOf(
                            walkDetailCEntity.coordinates[selectingPathIndex].slice(0..minMapIdx),
                            walkDetailCEntity.coordinates[selectingPathIndex].slice(minMapIdx until walkDetailCEntity.coordinates[selectingPathIndex].size)
                        )
                        multipartPaths[selectingPathIndex].colorParts = listOf(
                            MultipartPathOverlay.ColorPart(
                                getColor(R.color.black_light),
                                Color.WHITE,
                                getColor(R.color.black_light),
                                getColor(R.color.black_light)
                            ),
                            MultipartPathOverlay.ColorPart(
                                getColor(R.color.primary),
                                Color.WHITE,
                                getColor(R.color.primary),
                                getColor(R.color.primary)
                            ),
                        )
                    } else {    //시작점, 끝점 모두 움직였을 때
                        multipartPaths[selectingPathIndex].coordParts = listOf(
                            walkDetailCEntity.coordinates[selectingPathIndex].slice(0..minMapIdx),
                            walkDetailCEntity.coordinates[selectingPathIndex].slice(minMapIdx..maxMapIdx),
                            walkDetailCEntity.coordinates[selectingPathIndex].slice(maxMapIdx until walkDetailCEntity.coordinates[selectingPathIndex].size)
                        )
                        multipartPaths[selectingPathIndex].colorParts = listOf(
                            MultipartPathOverlay.ColorPart(
                                getColor(R.color.black_light),
                                Color.WHITE,
                                getColor(R.color.black_light),
                                getColor(R.color.black_light)
                            ),
                            MultipartPathOverlay.ColorPart(
                                getColor(R.color.primary),
                                Color.WHITE,
                                getColor(R.color.primary),
                                getColor(R.color.primary)
                            ),
                            MultipartPathOverlay.ColorPart(
                                getColor(R.color.black_light),
                                Color.WHITE,
                                getColor(R.color.black_light),
                                getColor(R.color.black_light)
                            )
                        )
                    }
                }

                binding.courseSetResetBtn.visibility = View.VISIBLE //초기화 버튼 VISIBLE
            }

            override fun onStartTrackingTouch(
                view: com.jaygoo.widget.RangeSeekBar?,
                isLeft: Boolean
            ) {
            }

            override fun onStopTrackingTouch(
                view: com.jaygoo.widget.RangeSeekBar?,
                isLeft: Boolean
            ) {
            }
        })

        //초기화 버튼 클릭 리스너
        binding.courseSetResetBtn.setOnClickListener {
            selectingPathIndex = 0

            if (walkDetailCEntity.coordinates.size > 1) {  //여러 구간으로 나뉜 코스일 때
                clickedCourseIndex.clear()  //클릭한 구간의 스택을 저장하는 리스트 clear
                binding.courseSetSectionTv.text = "${selectingPathIndex + 1}/${walkDetailCEntity.coordinates.size}구간"   //몇구간인지 보여주기
            }

            multipartPaths.clear()  //경로 리스트 clear
            courseSetModels.clear() //CourseSetActivity 에서 사용되는 데이터 모델 리스트 clear

            initCourseSetModels()   //CourseSetActivity 에서 사용되는 데이터 모델 리스트 세팅

            if (walkDetailCEntity.coordinates.size == 1)
                getSinglePathOverlay(walkDetailCEntity.coordinates, map)
            else
                getMultiPathOverlay(walkDetailCEntity.coordinates, map)

            //전체 경로가 보이도록 카메라 옮기기
            getPathBounds(walkDetailCEntity.coordinates)?.let {
                map.moveCamera(CameraUpdate.fitBounds(it))
            }
        }

        //취소 텍스트뷰 클릭 리스너
        binding.courseSetCancelTv.setOnClickListener {
            actionFrag.show(supportFragmentManager, null)
        }

        //다음 텍스트뷰 클릭 리스너
        binding.courseSetNextTv.setOnClickListener {
            //WalkDetailCEntity -> RecommendEntity 로 변환해서 CourseShareActivity 에 전달
            val intent: Intent = Intent(this@CourseSetActivity, CourseShareActivity::class.java)
            val selectedPathCoords: MutableList<MutableList<LatLng>> = mutableListOf()
            for (model in courseSetModels) {
                if (model.isSelected)
                    selectedPathCoords.add(model.selectedCoords)
            }
            val courseTime = (walkDetailCEntity.walkTime * (getTotalCnt(selectedPathCoords).toDouble() / getTotalCnt(walkDetailCEntity.coordinates).toDouble())).toInt()
            val recommendEntity: RecommendEntity = RecommendEntity(
                "",
                "",
                selectedPathCoords,
                walkDetailCEntity.hashtags,
                "",
                calDistance(selectedPathCoords),
                courseTime,
                walkDetailCEntity.walkIdx,
                ""
            )

            intent.putExtra("recommendEntity", recommendEntity)
            startActivity(intent)
        }
    }

    private fun initActionFrag() {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", getString(R.string.msg_cancel_course_share))
        bundle.putString("desc", getString(R.string.msg_no_restore_shared_content))
        bundle.putString("left", getString(R.string.action_cancel))
        bundle.putString("right", getString(R.string.action_delete))

        actionFrag = ActionDialogFragment()
        actionFrag.arguments = bundle
        actionFrag.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun leftAction(action: String) {
            }

            override fun rightAction(action: String) {
                finish()
            }
        })
    }

    private fun changeRangeBar(leftValue: Float, rightValue: Float) {
        binding.courseSetRsbSub.setMinThumbValue(leftValue.toInt())
        binding.courseSetRsbSub.setMaxThumbValue(rightValue.toInt())
        binding.courseSetRsb.setProgress(leftValue, rightValue)
    }

    private fun getTotalCnt(list: MutableList<MutableList<LatLng>>): Int {
        var cnt: Int = 0
        for (section in list) {
            cnt += section.size
        }

        return cnt
    }

    //거리 계산 함수
    private fun calDistance(coords: List<List<LatLng>>): Double {
        var distance: Double = 0.0

        for (section in coords) {
            var sectionDistance: Double = 0.0
            for (i in 0 until section.size-1) {
                sectionDistance += section[i].distanceTo(section[i+1])
            }
            distance += sectionDistance
        }

        return distance / 1000  //m -> km
    }

    private fun observe() {
        vm.mutableErrorType.observe(this, Observer {
            binding.courseSetLoadingPb.visibility = View.INVISIBLE

            when (it) {
                ErrorType.NETWORK -> {
                    networkErrSb = Snackbar.make(
                        binding.root,
                        getString(R.string.error_network),
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(getString(R.string.action_retry)) {
                        binding.courseSetLoadingPb.visibility = View.VISIBLE
                        vm.getWalkDetailCEntity(walkNumber)
                    }

                    networkErrSb.show()
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    startErrorActivity("CourseSetActivity")
                }
            }
        })

        vm.walkDetailCEntity.observe(this, Observer {
            binding.courseSetLoadingPb.visibility = View.INVISIBLE

            walkDetailCEntity = it

            initCourseSetModels()

            if (walkDetailCEntity.coordinates.size == 1) {   //단일 경로일 때
                getSinglePathOverlay(walkDetailCEntity.coordinates, map)   //지도에 경로 그리기
                binding.courseSetSectionTv.visibility = View.INVISIBLE  //몇번째 구간인지 보여주는(Ex.1/3구간) 텍스트뷰 INVISIBLE
            } else {    //여러 구간으로 나눠진 코스일 때
                getMultiPathOverlay(walkDetailCEntity.coordinates, map)    //지도에 경로 그리기

                //몇번째 구간인지 보여주는(Ex.1/3구간) 텍스트뷰 VISIBLE
                binding.courseSetSectionTv.text = "${selectingPathIndex + 1}/${walkDetailCEntity.coordinates.size}구간"
                binding.courseSetSectionTv.visibility = View.VISIBLE
            }

            //전체 경로가 보이도록 카메라 옮기기
            getPathBounds(walkDetailCEntity.coordinates)?.let {
                map.moveCamera(CameraUpdate.fitBounds(it))
            }
        })
    }
}