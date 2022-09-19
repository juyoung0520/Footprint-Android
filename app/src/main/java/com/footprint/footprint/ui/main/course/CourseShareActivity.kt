package com.footprint.footprint.ui.main.course

import android.Manifest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.footprint.footprint.R
import com.footprint.footprint.databinding.ActivityCourseShareBinding
import com.footprint.footprint.domain.model.RecommendEntity
import com.footprint.footprint.domain.model.UpdateCourseReqEntity
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.TagVerCSRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.CourseShareViewModel
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.skydoves.balloon.*
import gun0912.tedimagepicker.builder.TedImagePicker
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseShareActivity : BaseActivity<ActivityCourseShareBinding>(ActivityCourseShareBinding::inflate), TextWatcher {
    private val vm: CourseShareViewModel by viewModel()

    private lateinit var mode: String
    private lateinit var tagVerCSRVAdapter: TagVerCSRVAdapter
    private lateinit var balloon: Balloon
    private lateinit var actionFrag: ActionDialogFragment
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var networkErrSb: Snackbar
    private lateinit var recommendEntity: RecommendEntity
    private lateinit var updateCourseReqEntity: UpdateCourseReqEntity

    //퍼미션 확인 후 콜백 리스너
    private val permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            goGallery()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
        }
    }

    override fun initAfterBinding() {
        //키보드 감지 도구 설정
        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            //키보드가 올라와 있을 땐 NestedScrollView 의 높이를 '(전체 디바이스 높이) - (키보드 높이) - 18dp' 로 설정.
            //이렇게 해야 산책 코스 설명에서 줄바꿈이 발생했을 때 스크롤이 잘됨.
            onShowKeyboard = { keyboardHeight ->
                binding.courseShareCourseDescEt.setPadding(0, 0, 0, keyboardHeight)
            },
            //키보드가 내려갔을 땐 NestedScrollView 의 높이를 WRAP_CONTENT 로 설정.
            onHideKeyboard = {
                binding.courseShareCourseDescEt.setPadding(0, 0, 0, convertDpToPx(baseContext, 18))
            }
        )

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

        binding.courseShareThumbnailBaseIv.clipToOutline = true //이미지뷰 테두리 라운드 적용

        initAdapter()
        initDialog()
        setMyEventListener()
        observe()

        if (intent.hasExtra("courseName")) {
            mode = "update"
            vm.getCourse(intent.getStringExtra("courseName")!!)
        } else {
            mode = "create"
            //CourseSetActivity 로부터 전달 받은 recommendEntity 를 활용하여 데이터 바인딩 시작
            recommendEntity = intent.getParcelableExtra<RecommendEntity>("recommendEntity")!!   //CourseShareActivity 로부터 전달받은 코스 상세정보 데이터
            bindDataVerCreate()
        }
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

    private fun initDialog() {
        actionFrag = ActionDialogFragment()
        actionFrag.setMyDialogCallback(object: ActionDialogFragment.MyDialogCallback {
            override fun leftAction(action: String) {
            }

            //공유 버튼 클릭 리스너
            override fun rightAction(action: String) {
                //프로그래스바 VISIBLE
                binding.courseShareLoadingPb.visibility = View.VISIBLE

                //사용자가 입력한 데이터들을 recommendEntity 에 저장하는 함수 호출
                setRecommendEntity()
            }
        })
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

        //완료 버튼 클릭 리스너 -> AcitionDialog 띄우기
        binding.courseShareCompleteTv.setOnClickListener {
            hideKeyboard(binding.root)  //키보드 숨기기

            if (mode=="create") {
                val bundle: Bundle = Bundle()
                bundle.putString("msg", "‘${binding.courseShareCourseNameEt.text}’을 공유할까요?")
                bundle.putString("left", getString(R.string.action_cancel))
                bundle.putString("right", getString(R.string.action_share))

                actionFrag.arguments = bundle
                actionFrag.show(supportFragmentManager, null)
            } else {
                binding.courseShareLoadingPb.visibility = View.VISIBLE

                updateCourseReqEntity.courseName = binding.courseShareCourseNameEt.text.toString()
                updateCourseReqEntity.hashtags = tagVerCSRVAdapter.getCheckedTags()
                updateCourseReqEntity.description = binding.courseShareCourseDescEt.text.toString()

                vm.updateCourse(baseContext, updateCourseReqEntity)
            }
        }
    }

    private fun bindDataVerCreate() {
        getAddress(recommendEntity.coordinates[0][0])   //코스 위치
        binding.courseShareCourseLengthTv.text = "${String.format("%.2f", recommendEntity.length)}km"  //코스 길이
        binding.courseShareCourseTimeTv.text = "약 ${recommendEntity.courseTime}분"    //소요 시간

        //해시태그
        if (recommendEntity.hashtags != null)
            tagVerCSRVAdapter.setData(recommendEntity.hashtags!!, recommendEntity.hashtags!!)

        //바인딩 끝났으니까 프로그래스바 INVISIBLE
        binding.courseShareLoadingPb.visibility = View.INVISIBLE
    }

    // 좌표 -> 주소 변환
    private fun getAddress(coords: LatLng) {
        if (!isNetworkAvailable(applicationContext)) {
            binding.courseShareLoadingPb.visibility = View.INVISIBLE

            networkErrSb = Snackbar.make(
                binding.root,
                getString(R.string.error_network),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(getString(R.string.action_retry)) {
                binding.courseShareLoadingPb.visibility = View.VISIBLE
                vm.getAddress("${coords.longitude},${coords.latitude}")
            }

            networkErrSb.show()
        } else {
            vm.getAddress("${coords.longitude},${coords.latitude}")
        }
    }

    //코스 저장 전 recommendEntity 에 데이터 저장하기
    private fun setRecommendEntity() {
        //주소
        if (binding.courseShareLocationEt.visibility==View.VISIBLE)
            recommendEntity.address = binding.courseShareLocationEt.text.toString()
        else
            recommendEntity.address = binding.courseShareCourseLocationTv.text.toString()
        recommendEntity.courseName = binding.courseShareCourseNameEt.text.toString()    //코스 이름
        recommendEntity.hashtags = tagVerCSRVAdapter.getCheckedTags()   //해시 태그
        recommendEntity.description = binding.courseShareCourseDescEt.text.toString()   //코스 설명

        //recommendEntity 에 저장 끝나면 코스 저장 API 호출
        vm.saveCourse(baseContext, recommendEntity)
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
                if (mode=="create") {    //생성
                    recommendEntity.courseImg = uri.toString()  //recommentEntity 의 courseImg 에 uri String 형태로 저장
                } else {    //수정
                    updateCourseReqEntity.courseImg = uri.toString()    //updateCourseReqEntity 의 courseImg 에 uri String 형태로 저장
                }

                changePhotoIvUI(uri.toString())
            }
    }

    private fun validate(): Boolean {
        return if (binding.courseShareLocationEt.visibility == View.VISIBLE) {
            (binding.courseShareCourseNameEt.text.isNotBlank() && binding.courseShareLocationEt.text.isNotBlank())
        } else {
            binding.courseShareCourseNameEt.text.isNotBlank()
        }
    }

    private fun changePhotoIvUI(url: String) {
        binding.courseSharePhotoIv.visibility = View.INVISIBLE  //갤러리 아이콘 감추기
        binding.courseSharePhotoEditIv.visibility = View.VISIBLE    //갤러리 편집 아이콘 보이기
        Glide.with(this).load(url).into(binding.courseShareThumbnailBaseIv)
    }

    private fun observe() {
        vm.mutableErrorType.observe(this, Observer {
            binding.courseShareLoadingPb.visibility = View.INVISIBLE

            when (it) {
                ErrorType.NETWORK -> {  //네트워크 에러
                    when (vm.getErrorType()) {
                        "getAddress" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) { vm.getAddress("${recommendEntity.coordinates[0][0].longitude},${recommendEntity.coordinates[0][0].latitude}") }
                        "saveCourse" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_LONG)
                        "getCourse" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) { vm.getCourse(intent.getStringExtra("courseName")!!) }
                        "updateCourse" -> networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_LONG)
                    }

                    networkErrSb.show()
                }
                ErrorType.ADDRESS -> {  //주소를 받아오지 못할 때 -> 주소 직접 입력
                    binding.courseShareCourseLocationTv.visibility = View.INVISIBLE
                    binding.courseShareLocationPinIv.visibility = View.INVISIBLE
                    binding.courseShareLocationEt.visibility = View.VISIBLE
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER, ErrorType.S3 -> {
                    startErrorActivity("CourseShareActivity")
                }
            }
        })

        vm.saveCourseRes.observe(this, Observer {
            binding.courseShareLoadingPb.visibility = View.INVISIBLE

            if (it.isSuccess) {
                showToast(it.result!!)  //코스가 등록되었습니다 토스트 메시지 띄우기
                this@CourseShareActivity.finishAffinity()   //CourseSelectActivity, CourseShareActivity 모두 종료 -> 마이-내 추천코스 화면으로 이동
            } else {
                when (it.code) {
                    2151 -> showToast(it.message)   //중복된 이름
                    else -> startErrorActivity("CourseShareActivity")
                }
            }
        })

        //주소 받아오면 -> 주소 보여주기
        vm.address.observe(this, Observer {
            binding.courseShareCourseLocationTv.visibility = View.VISIBLE
            binding.courseShareLocationPinIv.visibility = View.VISIBLE
            binding.courseShareLocationEt.visibility = View.INVISIBLE

            binding.courseShareCourseLocationTv.text = it
        })

        vm.course.observe(this, Observer {
            binding.courseShareLoadingPb.visibility = View.INVISIBLE

            updateCourseReqEntity = it.run {
                 UpdateCourseReqEntity(courseIdx, courseName, courseImg, coordinates, allHashtags, address, distance, courseTime, walkIdx, description)
            }

            if (updateCourseReqEntity.courseImg.isNotBlank())
                changePhotoIvUI(updateCourseReqEntity.courseImg)

            binding.courseShareCourseLocationTv.text = updateCourseReqEntity.address
            binding.courseShareCourseLengthTv.text = "${updateCourseReqEntity.length}km"
            binding.courseShareCourseTimeTv.text = "약 ${updateCourseReqEntity.courseTime}분"
            binding.courseShareCourseNameEt.setText(updateCourseReqEntity.courseName)
            tagVerCSRVAdapter.setData(updateCourseReqEntity.hashtags, it.selectedHashtags)
            binding.courseShareCourseDescEt.setText(updateCourseReqEntity.description)
        })

        vm.updateCourseRes.observe(this, Observer {
            binding.courseShareLoadingPb.visibility = View.INVISIBLE

            if (it.isSuccess) {
                showToast(it.result!!)  //코스가 수정되었습니다 토스트 메시지 띄우기
                this@CourseShareActivity.finishAffinity()   //CourseSelectActivity, CourseShareActivity 모두 종료 -> 마이-내 추천코스 화면으로 이동
            } else {
                when (it.code) {
                    2151 -> showToast(it.message)   //중복된 이름
                    else -> startErrorActivity("CourseShareActivity")
                }
            }
        })
    }
}