package com.footprint.footprint.ui.main.course

import android.Manifest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityCourseShareBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.TagVerCSRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.MapViewModel
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.skydoves.balloon.*
import gun0912.tedimagepicker.builder.TedImagePicker
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseShareActivity : BaseActivity<ActivityCourseShareBinding>(ActivityCourseShareBinding::inflate), TextWatcher {
    private val mapVm: MapViewModel by viewModel<MapViewModel>()

    private lateinit var tagVerCSRVAdapter: TagVerCSRVAdapter
    private lateinit var balloon: Balloon
    private lateinit var actionFrag: ActionDialogFragment
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var selectedCoords: MutableList<MutableList<LatLng>>
    private lateinit var networkErrSb: Snackbar

    //퍼미션 확인 후 콜백 리스너
    private val permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            goGallery()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
        }
    }

    override fun initAfterBinding() {
        selectedCoords = intent.getSerializableExtra("coords") as MutableList<MutableList<LatLng>> //CourseSetActivity 에서 전달받은 선택된 좌표 리스트

        //키보드 감지 도구 설정
        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            //키보드가 올라와 있을 땐 NestedScrollView 의 높이를 '(전체 디바이스 높이) - (키보드 높이) - 18dp' 로 설정.
            //이렇게 해야 산책 코스 설명에서 줄바꿈이 발생했을 때 스크롤이 잘됨.
            onShowKeyboard = { keyboardHeight ->
                var params: ViewGroup.LayoutParams = binding.courseShareNsv.layoutParams
                params.height = getDeviceHeight() - keyboardHeight - convertDpToPx(applicationContext, 18)
                binding.courseShareNsv.layoutParams = params
            },
            //키보드가 내려갔을 땐 NestedScrollView 의 높이를 WRAP_CONTENT 로 설정.
            onHideKeyboard = {
                var params: ViewGroup.LayoutParams = binding.courseShareNsv.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.courseShareNsv.layoutParams = params
            }
        )

        initAdapter()
        setMyEventListener()
        observe()

        getAddress(selectedCoords[0][0])   //코스 위치
        binding.courseShareCourseLengthTv.text = "${String.format("%.2f", calDistance(selectedCoords))}km"  //코스 길이
        binding.courseShareCourseTimeTv.text = "약 ${intent.getIntExtra("time", 0)}분"    //소요 시간
        binding.courseShareThumbnailBaseIv.clipToOutline = true //이미지뷰 테두리 라운드 적용

        //태그 설명 말풍선 설정
        balloon = Balloon.Builder(applicationContext)
            .setWidth(253)
            .setTextSize(convertDpToSp(applicationContext, 12).toFloat())
            .setHeight(BalloonSizeSpec.WRAP)
            .setText(getString(R.string.msg_course_share_tag_desc))
            .setTextTypeface(R.font.namusquareround)
            .setTextColorResource(R.color.white)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setPaddingHorizontal(18)
            .setPaddingVertical(6)
            .setCornerRadius(40f)
            .setMarginHorizontal(24)
            .setMarginBottom(5)
            .setBackgroundColorResource(R.color.black_light)
            .setBalloonAnimation(BalloonAnimation.FADE)
            .setAutoDismissDuration(3000L)
            .build()
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }

    override fun onDestroy() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroy()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        binding.courseShareCompleteTv.isEnabled = validate()    //유효성 검사
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    private fun initAdapter() {
        tagVerCSRVAdapter = TagVerCSRVAdapter(applicationContext)
        tagVerCSRVAdapter.setData(arrayListOf<String>("#서울 망원동", "#혼자서", "#어쩌구", "#저쩌구"))
        tagVerCSRVAdapter.setMyClickListener(object : TagVerCSRVAdapter.MyClickListener {
            override fun checked(isChecked: Boolean) {
                hideKeyboard(binding.root)  //키보드 숨기기

                binding.courseShareCourseNameEt.clearFocus()    //이름 EditText 포커스 제거
                binding.courseShareCourseDescEt.clearFocus()    //설명 EditText 포커스 제거

                binding.courseShareCompleteTv.isEnabled = validate()    //유효성 검사
            }
        })
        binding.courseShareTagRv.adapter = tagVerCSRVAdapter
    }

    private fun setMyEventListener() {
        //뒤로가기 아이콘 클릭 리스너
        binding.courseShareBackIv.setOnClickListener {
            finish()
        }

        //갤러리 이미지뷰 클릭 리스너
        binding.courseSharePhotoIv.setOnClickListener {
            checkPermission()
        }

        //갤러리 편집 아이콘 이미지뷰 클릭 리스너
        binding.courseSharePhotoEditIv.setOnClickListener {
            checkPermission()
        }

        //산책코스 이름, 설명 EditText TextWatcher 리스너 등록
        binding.courseShareCourseNameEt.addTextChangedListener(this)
        binding.courseShareLocationEt.addTextChangedListener(this)

        //태그 관련 도움말 아이콘 이미지뷰 클릭 리스너
        binding.courseShareQuestionIv.setOnClickListener {
            binding.courseShareQuestionIv.showAlignTop(balloon) //말풍선 띄우기
            hideKeyboard(binding.root)  //키보드 내리기
            binding.courseShareCourseNameEt.clearFocus()    //이름 EditText 포커스 제거
            binding.courseShareCourseDescEt.clearFocus()    //설명 EditText 포커스 제거
        }

        binding.courseShareCompleteTv.setOnClickListener {
            hideKeyboard(binding.root)  //키보드 숨기기

            val bundle: Bundle = Bundle()
            bundle.putString("msg", "‘${binding.courseShareCourseNameEt.text}’을 공유할까요?")
            bundle.putString("left", getString(R.string.action_cancel))
            bundle.putString("right", getString(R.string.action_share))

            actionFrag = ActionDialogFragment()
            actionFrag.arguments = bundle
            actionFrag.setMyDialogCallback(object: ActionDialogFragment.MyDialogCallback {
                override fun leftAction(action: String) {
                }

                override fun rightAction(action: String) {
                    this@CourseShareActivity.finishAffinity()   //CourseSelectActivity, CourseShareActivity 모두 종료
                }
            })

            actionFrag.show(supportFragmentManager, null)
        }
    }

    // 좌표 -> 주소 변환
    private fun getAddress(coords: LatLng) {
        mapVm.getAddress("${coords.longitude},${coords.latitude}")
    }

    //카메라, 저장소 퍼미션 확인
    private fun checkPermission() {
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.error_permission_denied))
            .setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    //TedImagePicker 라이브러리를 활용해 갤러리 화면으로 이동하기
    private fun goGallery() {
        TedImagePicker.with(this)
            .start { uri ->
                binding.courseSharePhotoIv.visibility = View.INVISIBLE  //갤러리 아이콘 감추기
                binding.courseSharePhotoEditIv.visibility = View.VISIBLE    //갤러리 편집 아이콘 보이기
                binding.courseShareThumbnailBaseIv.setImageURI(uri) //선택한 이미지 보여주기
            }
    }

    private fun validate(): Boolean {
        return if (binding.courseShareLocationEt.visibility == View.VISIBLE) {
            (binding.courseShareCourseNameEt.text.isNotBlank() && binding.courseShareLocationEt.text.isNotBlank())
        } else {
            binding.courseShareCourseNameEt.text.isNotBlank()
        }
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
        mapVm.mutableErrorType.observe(this, Observer {

            when (it) {
                ErrorType.NETWORK -> {
                    when (mapVm.getErrorType()) {
                        "getAddress" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) { mapVm.getAddress("${selectedCoords[0][0].longitude},${selectedCoords[0][0].latitude}") }
                    }

                    networkErrSb.show()
                }
                ErrorType.ADDRESS -> {  //주소를 받아오지 못할 때 -> 주소 직접 입력
                    binding.courseShareCourseLocationTv.visibility = View.INVISIBLE
                    binding.courseShareLocationPinIv.visibility = View.INVISIBLE
                    binding.courseShareLocationEt.visibility = View.VISIBLE
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    startErrorActivity("CourseShareActivity")
                }
            }
        })

        //주소 받아오면 -> 주소 보여주기
        mapVm.address.observe(this, Observer {
            binding.courseShareCourseLocationTv.visibility = View.VISIBLE
            binding.courseShareLocationPinIv.visibility = View.VISIBLE
            binding.courseShareLocationEt.visibility = View.INVISIBLE

            binding.courseShareCourseLocationTv.text = it
        })
    }
}