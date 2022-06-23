package com.footprint.footprint.ui.main.course

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityCourseSelectBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.jaygoo.widget.OnRangeChangedListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.*

class CourseSelectActivity : BaseActivity<ActivityCourseSelectBinding>(ActivityCourseSelectBinding::inflate), OnMapReadyCallback {
    private val path = PathOverlay()    //단일 경로
    private val multipartPath = MultipartPathOverlay()  //멀티 경로

    private lateinit var pathCoords: List<LatLng>
    private lateinit var startMarker: Marker
    private lateinit var endMarker: Marker
    private lateinit var map: NaverMap
    private lateinit var actionFrag: ActionDialogFragment

    override fun initAfterBinding() {
        pathCoords = listOf(
            LatLng(37.57152, 126.97714),
            LatLng(37.57052, 126.97814),
            LatLng(37.56952, 126.97914),
            LatLng(37.56852, 126.98014),
            LatLng(37.56752, 126.98114),
            LatLng(37.56607, 126.98268),
            LatLng(37.56507, 126.98568),
            LatLng(37.56445, 126.98407),
            LatLng(37.56345, 126.98307),
            LatLng(37.56245, 126.98207),
            LatLng(37.56145, 126.98107),
            LatLng(37.56045, 126.98007),
            LatLng(37.55945, 126.97907),
            LatLng(37.55855, 126.97822)
        )

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.course_select_map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.course_select_map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this)

        //RangeSeekbar 설정
        binding.courseSelectRsb.leftSeekBar.thumbDrawableId = R.drawable.ic_start_marker
        binding.courseSelectRsb.rightSeekBar.thumbDrawableId = R.drawable.ic_end_marker
        binding.courseSelectRsb.setProgress(0f, 100f)

        setMyEventListener()
        initActionFrag()
    }

    //뒤로가기 버튼 누르면 공유를 취소할건지 물어보는 action 다이얼로그 화면 띄우기
    override fun onBackPressed() {
        actionFrag.show(supportFragmentManager, null)
    }

    //네이버 지도 세팅
    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap

        //단일 경로 설정(디폴트)
        path.coords = pathCoords
        path.width = 30
        path.outlineWidth = 10
        path.color = getColor(R.color.primary)
        path.outlineColor = Color.WHITE
        path.map = map

        //멀티 경로 설정
        multipartPath.width = 30
        multipartPath.outlineWidth = 10

        startMarker = Marker()
        startMarker.position = path.coords[0]
        startMarker.icon = OverlayImage.fromResource(R.drawable.ic_donut_secondary)
        startMarker.map = map

        endMarker = Marker()
        endMarker.position = path.coords[path.coords.size-1]
        endMarker.icon = OverlayImage.fromResource(R.drawable.ic_donut_primary)
        endMarker.map = map
    }

    private fun setMyEventListener() {
        binding.courseSelectRsb.setOnRangeChangedListener(object: OnRangeChangedListener {
            override fun onRangeChanged(
                view: com.jaygoo.widget.RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                //아래 RangeSeekbar 조절하기
                binding.courseSelectRsbSub.setMinThumbValue(leftValue.toInt())
                binding.courseSelectRsbSub.setMaxThumbValue(rightValue.toInt())

                //지도에서 시작, 끝 지점 변경하기
                //시작, 끝 마커가 위치할 좌표 인덱스 설정하기
                var minMapIdx = (pathCoords.size * leftValue / 100 - 1).toInt()
                var maxMapIdx = (pathCoords.size * rightValue / 100 - 1).toInt()
                if (minMapIdx<0)
                    minMapIdx = 0
                else if (minMapIdx==pathCoords.size)
                    minMapIdx--
                if (maxMapIdx==pathCoords.size)
                    maxMapIdx--
                else if (maxMapIdx<0)
                    maxMapIdx = 0

                if (minMapIdx<1 && maxMapIdx>=pathCoords.size-1) {  //시작, 끝 좌표 모두 제자리에 있을 때
                    multipartPath.map = null

                    path.color = getColor(R.color.primary)
                    path.map = map

                    binding.couseSelectResetBtn.visibility = View.INVISIBLE
                } else if (minMapIdx==maxMapIdx) {  //시작, 끝 같은 위치
                    multipartPath.map = null

                    path.color = getColor(R.color.white_dark)
                    path.map = map

                    binding.couseSelectResetBtn.visibility = View.VISIBLE
                } else if (minMapIdx<1) {   //시작 제자리, 끝만 움직임
                    path.map = null

                    multipartPath.coordParts = listOf(
                        pathCoords.slice(0 .. maxMapIdx),
                        pathCoords.slice(maxMapIdx until pathCoords.size)
                    )
                    multipartPath.colorParts = listOf(
                        MultipartPathOverlay.ColorPart(getColor(R.color.primary), Color.WHITE, getColor(R.color.primary), getColor(R.color.primary)),
                        MultipartPathOverlay.ColorPart(getColor(R.color.white_dark), Color.WHITE, getColor(R.color.white_dark), getColor(R.color.white_dark))
                    )
                    multipartPath.map = map

                    binding.couseSelectResetBtn.visibility = View.VISIBLE
                } else if (maxMapIdx>=pathCoords.size-1) {  //시작 움직임, 끝 제자리
                    path.map = null

                    multipartPath.coordParts = listOf(
                        pathCoords.slice(0 .. minMapIdx),
                        pathCoords.slice(minMapIdx until pathCoords.size)
                    )
                    multipartPath.colorParts = listOf(
                        MultipartPathOverlay.ColorPart(getColor(R.color.white_dark), Color.WHITE, getColor(R.color.white_dark), getColor(R.color.white_dark)),
                        MultipartPathOverlay.ColorPart(getColor(R.color.primary), Color.WHITE, getColor(R.color.primary), getColor(R.color.primary))
                    )
                    multipartPath.map = map

                    binding.couseSelectResetBtn.visibility = View.VISIBLE
                } else if (minMapIdx != maxMapIdx) {    //시작, 끝 모두 움직임(둘이 겹칠 때 제외)
                    path.map = null

                    multipartPath.coordParts = listOf(
                        pathCoords.slice(0 .. minMapIdx),
                        pathCoords.slice(minMapIdx .. maxMapIdx),
                        pathCoords.slice(maxMapIdx until pathCoords.size)
                    )
                    multipartPath.colorParts = listOf(
                        MultipartPathOverlay.ColorPart(getColor(R.color.white_dark), Color.WHITE, getColor(R.color.white_dark), getColor(R.color.white_dark)),
                        MultipartPathOverlay.ColorPart(getColor(R.color.primary), Color.WHITE, getColor(R.color.primary), getColor(R.color.primary)),
                        MultipartPathOverlay.ColorPart(getColor(R.color.white_dark), Color.WHITE, getColor(R.color.white_dark), getColor(R.color.white_dark))
                    )
                    multipartPath.map = map

                    binding.couseSelectResetBtn.visibility = View.VISIBLE
                }

                //시작, 끝 마커 지도상에 표시하기기
                startMarker.position = pathCoords[minMapIdx]
                endMarker.position = pathCoords[maxMapIdx]
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
        binding.couseSelectResetBtn.setOnClickListener {
            binding.courseSelectRsb.setProgress(0f, 100f)
        }

        //취소 텍스트뷰 클릭 리스너
        binding.courseSelectCancelTv.setOnClickListener {
            actionFrag.show(supportFragmentManager, null)
        }

        //다음 텍스트뷰 클릭 리스너
        binding.courseSelectNextTv.setOnClickListener {
            startNextActivity(CourseShareActivity::class.java)
        }
    }

    private fun initActionFrag() {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", getString(R.string.msg_cancel_course_share))
        bundle.putString("desc", getString(R.string.msg_no_restore_shared_content))
        bundle.putString("action", getString(R.string.action_delete))

        actionFrag = ActionDialogFragment()
        actionFrag.arguments = bundle
        actionFrag.setMyDialogCallback(object: ActionDialogFragment.MyDialogCallback {
            override fun action1(isAction: Boolean) {
                if (isAction)   //삭제 텍스트뷰를 눌렀을 때 액티비티 종료
                    finish()
            }

            override fun action2(isAction: Boolean) {
            }
        })
    }
}