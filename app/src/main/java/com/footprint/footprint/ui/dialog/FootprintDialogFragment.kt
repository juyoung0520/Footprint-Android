package com.footprint.footprint.ui.dialog

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.footprint.footprint.R
import com.footprint.footprint.data.model.FootprintModel
import com.footprint.footprint.databinding.FragmentFootprintDialogBinding
import com.footprint.footprint.ui.adapter.PhotoRVAdapter
import com.footprint.footprint.utils.DialogFragmentUtils
import com.footprint.footprint.utils.getAbsolutePathByBitmap
import com.footprint.footprint.utils.uriToBitmap
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.santalu.textmatcher.rule.HashtagRule
import com.santalu.textmatcher.style.HashtagStyle
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class FootprintDialogFragment() : DialogFragment(), TextWatcher {
    private lateinit var binding: FragmentFootprintDialogBinding
    private lateinit var photoRVAdapter: PhotoRVAdapter
    private lateinit var footprint: FootprintModel
    private lateinit var myDialogCallback: MyDialogCallback

    private var textMatcherFlag: Int = 1
    private var isUpdate: Boolean = false

    private val imgList: ArrayList<String> = arrayListOf<String>()  //선택한 사진을 저장하는 변수

    //퍼미션 확인 후 콜백 리스너
    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            goGallery()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
        }
    }

    interface MyDialogCallback {
        fun sendFootprint(footprint: FootprintModel)
        fun sendUpdatedFootprint(footprint: FootprintModel)
        fun cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFootprintDialogBinding.inflate(inflater, container, false)

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        setHashTag()    //해시태그 관련 설정(TextMatcher)
        initAdapter()   //뷰페이저 어댑터 설정
        setMyClickListener()    //클릭 리스너 설정

        val footprintStr = arguments?.getString("footprint", "")    //이전 화면으로부터 전달 받는 발자국 데이터
        if (footprintStr!=null) {   //발자국 데이터가 있다는 건 수정 화면이라는 의미
            isUpdate = true
            footprint = Gson().fromJson(footprintStr, FootprintModel::class.java)
            setUI(footprint)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        //전체 프래그먼트 크기 설정
        DialogFragmentUtils.dialogFragmentResize(
            requireContext(),
            this@FootprintDialogFragment,
            0.9f,
            0.64f
        )
    }

    //다이얼로그가 종료되면 WalkMapFragment 에서 타이머를 재시작 할 수 있도록 cancel 콜백 함수 실행
    override fun onDestroyView() {
        myDialogCallback.cancel()
        super.onDestroyView()
    }

    //TextWatcher
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //해시태그가 다시 5개 이하로 바뀌면 hashTag 를 찾는 textMatcher 실행
        if (p0!!.isNotBlank()) {
            if (p0.last().toString() == "#" && textMatcherFlag == 0) {
                val hashtags = findHashTag()
                if (hashtags.size <= 5) {
                    textMatcherFlag = 1
                    setHashTag()
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {

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
            .check();
    }

    //TedImagePicker 라이브러리를 활용해 갤러리 화면으로 이동하기
    private fun goGallery() {
        val selectedUri: ArrayList<Uri> = arrayListOf()
        imgList.forEach {
            if (it.startsWith("https://"))  //발자국 수정일 때 사진 재선택하기
                selectedUri.add(Uri.parse(it))
            else    //발자국 추가일 때 사진 재선택하기
                selectedUri.add(Uri.fromFile(File(it)))
        }

        TedImagePicker.with(requireContext())
            .selectedUri(selectedUri)
            .buttonText(R.string.action_next)
            .buttonBackground(R.color.white)
            .buttonTextColor(R.color.primary)
            .showTitle(false)
            .backButton(R.drawable.ic_cancel)
            .savedDirectoryName("발자국")
            .max(5, R.string.error_photo_cnt_exceeded)
            .startMultiImage { uriList ->
                imgList.clear()

                uriList.forEach {
                    if (it.toString().startsWith("https://")) {   //원래 저장돼 있던 이미지: url -> Bitmap 변환(비동기 방식)
                        Glide.with(this@FootprintDialogFragment).asBitmap().load(it).into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                imgList.add(getAbsolutePathByBitmap(requireContext(), resource))

                                //비동기 방식이라 사진이 모두 변환됐는지 확인하는 용도
                                if (imgList.size==uriList.size) {   //사진이 모두 변환됐으면 뷰페이저에 해당 이미지 데이터 전달
                                    photoRVAdapter.addImgList(imgList)
                                    binding.postDialogPhotoIndicator.setViewPager(binding.postDialogPhotoVp)
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })
                    } else    //새로 저장할 이미지
                        imgList.add(getAbsolutePathByBitmap(requireContext(), uriToBitmap(requireContext(), it)))
                }

                if (imgList.size==uriList.size) {
                    photoRVAdapter.addImgList(imgList)
                    binding.postDialogPhotoIndicator.setViewPager(binding.postDialogPhotoVp)
                }

                if (uriList.isEmpty()) {    //이미지를 선택하지 않았으면 -> 사진 추가하기 텍스트뷰 보여주기
                    setDeletePhotoUI()
                } else {    //이미지를 선택했다면 -> 사진 삭제하기 텍스트뷰 보여주기, 사진 뷰페이저&인디케이터 VISIBLE
                    binding.postDialogPhotoVp.visibility = View.VISIBLE
                    binding.postDialogPhotoIndicator.visibility = View.VISIBLE
                    binding.postDialogAddPhotoTv.text = getString(R.string.action_delete_photo)
                }
            }
    }

    //사진 뷰페이저 어댑터 초기화 함수
    private fun initAdapter() {
        photoRVAdapter = PhotoRVAdapter(0)

        //갤러리 아이콘 이미지뷰 클릭 리스너 -> 갤러리로 이동
        photoRVAdapter.setMyItemClickListener(object : PhotoRVAdapter.MyItemClickListener {
            override fun goGalleryClick() {
                goGallery()
            }
        })

        binding.postDialogPhotoVp.adapter = photoRVAdapter
    }

    //해시태그 관련 설정 함수
    private fun setHashTag() {
        //해시태그의 텍스트 스타일 설정 객체
        val hashtagStyle = HashtagStyle(
            textColor = ContextCompat.getColor(requireActivity(), R.color.primary),
            isBold = false
        )
        var hashtagRule = HashtagRule("-", hashtagStyle)
        binding.postDialogContentEt.addRule(hashtagRule)

        binding.postDialogContentEt.setOnMatchListener { rule, s ->
            if (s == "#") {
                //현재 EditText 에 작성된 해시태그의 개수를 찾기 위한 코드
                val hashtags = findHashTag()

                //해시태그가 5개보다 많아지면
                if (hashtags.size > 5) {
                    //"해시태그는 5개까지 작성할 수 있어요." 토스트 메세지 띄우기
                    Toast.makeText(requireContext(), getString(R.string.error_hashtag_exceed_cnt), Toast.LENGTH_SHORT).show()

                    textMatcherFlag = 0
                    binding.postDialogContentEt.addTextChangedListener(this)
                    binding.postDialogContentEt.removeRule(hashtagRule)
                }
            }
        }
    }

    //사용자가 입력한 내용 속에서 해시태그를 찾는 함수
    private fun findHashTag(): java.util.ArrayList<String> {
        val pattern = Regex("#([0-9a-zA-Z가-힣]*)")
        val matchedWords = pattern.findAll(binding.postDialogContentEt.text.toString())
        val hashtags: ArrayList<String> = arrayListOf()

        matchedWords.forEach {
            hashtags.add(it.value)
        }

        return hashtags
    }

    private fun setMyClickListener() {
        //취소 텍스트뷰 클릭 리스너 -> 프래그먼트 종료
        binding.postDialogCancelTv.setOnClickListener {
            dismiss()
        }

        //사진 추가하기/삭제하기 텍스트뷰 클릭 리스너 -> 갤러리 이동 전 퍼미션 체크
        binding.postDialogAddPhotoIv.setOnClickListener {
            if (binding.postDialogAddPhotoTv.text == getString(R.string.action_add_photo))
                checkPermission()
            else
                setDeletePhotoUI()
        }

        //사진 추가하기/삭제하기 이미지뷰 클릭 리스너 -> 갤러리 이동 전 퍼미션 체크
        binding.postDialogAddPhotoTv.setOnClickListener {
            if ((it as TextView).text == getString(R.string.action_add_photo))
                checkPermission()
            else
                setDeletePhotoUI()
        }

        //저장 텍스트뷰 클릭 리스너
        binding.postDialogSaveTv.setOnClickListener {
            if (binding.postDialogContentEt.text!!.isBlank() && imgList.isEmpty()) {  //유효성 검사 통과 X -> "사진이나 글을 추가해주세요." 다이얼로그 화면 띄우기
                val bundle: Bundle = Bundle()
                val msgDialogFragment: MsgDialogFragment = MsgDialogFragment()

                bundle.putString("msg", getString(R.string.msg_add_photo_or_writing))
                msgDialogFragment.arguments = bundle
                msgDialogFragment.show(requireActivity().supportFragmentManager, null)
            } else {    //유효성 검사 통과 -> 프래그먼트 종료, 콜백함수 호출
                dismiss()

                if (isUpdate)   //발자국 수정일 때
                    myDialogCallback.sendUpdatedFootprint(setFootprintData())
                else    //발자국 추가일 때
                    myDialogCallback.sendFootprint(setFootprintData())
            }
        }
    }

    //사용자가 입력한 발자국 데이터를 가져오는 함수
    private fun setFootprintData(): FootprintModel {
        //발자국 추가하기 화면일 경우에는 아직 footprint 객체가 초기화되기 전이므로 초기화 시키기
        if (!::footprint.isInitialized) {
            footprint = FootprintModel()

            val current = LocalDateTime.now(TimeZone.getTimeZone("Asia/Seoul").toZoneId())
            footprint.recordAt = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

        //해시태그
        var hashtags = findHashTag()
        if (hashtags.isNotEmpty() && hashtags.size > 5)
            hashtags = hashtags.slice(0..4) as java.util.ArrayList<String>
        footprint.hashtagList = hashtags

        footprint.coordinate = listOf(10.0, 12.0)   //발자국을 남긴 좌표
        footprint.write = binding.postDialogContentEt.text.toString()   //발자국 내용
        footprint.photos = imgList  //발자국 사진

        return footprint
    }

    //사진 삭제하기 텍스트뷰/이미지뷰를 눌렀을 때 호출되는 함수 -> 사진 추가하기로 바꾸고, 선택됐던 모든 이미지 삭제
    private fun setDeletePhotoUI() {
        binding.postDialogPhotoVp.visibility = View.GONE
        binding.postDialogPhotoIndicator.visibility = View.GONE
        binding.postDialogAddPhotoTv.text = getString(R.string.action_add_photo)
        imgList.clear()
        photoRVAdapter.clearImgList()
    }

    //발자국 수정하기 화면 상태일 때 사용자가 수정 전 입력했던 발자국 데이터를 바인딩하는 함수
    private fun setUI(footprint: FootprintModel) {
        binding.postDialogContentEt.setText(footprint.write)    //발자국 내용

        if (footprint.photos.isEmpty()) {    //사진이 없으면 "사진 선택하기" 버전
            setDeletePhotoUI()
        } else {    //사진이 있으면 "사진 삭제하기" 버전
            imgList.clear()
            imgList.addAll(footprint.photos)

            binding.postDialogPhotoVp.visibility = View.VISIBLE
            photoRVAdapter.addImgList(footprint.photos as ArrayList<String>)

            binding.postDialogPhotoIndicator.visibility = View.VISIBLE
            binding.postDialogPhotoIndicator.setViewPager(binding.postDialogPhotoVp)

            binding.postDialogAddPhotoTv.setText(R.string.action_delete_photo)
        }
    }

    fun setMyDialogCallback(myDialogCallback: MyDialogCallback) {
        this.myDialogCallback = myDialogCallback
    }
}