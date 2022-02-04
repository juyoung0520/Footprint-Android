package com.footprint.footprint.ui.dialog

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentFootprintDialogBinding
import com.footprint.footprint.data.model.FootprintModel
import com.footprint.footprint.ui.adapter.PhotoRVAdapter
import com.footprint.footprint.utils.DialogFragmentUtils
import com.google.gson.Gson
import gun0912.tedimagepicker.builder.TedImagePicker
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.santalu.textmatcher.rule.HashtagRule
import com.santalu.textmatcher.style.HashtagStyle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class FootprintDialogFragment() : DialogFragment(), TextWatcher {
    private lateinit var binding: FragmentFootprintDialogBinding
    private lateinit var photoRVAdapter: PhotoRVAdapter
    private lateinit var footprint: FootprintModel

    private var textMatcherFlag: Int = 1
    private var isUpdate: Boolean = false

    private val imgList: ArrayList<String> = arrayListOf<String>()  //선택한 사진을 저장하는 변수
    private val args: FootprintDialogFragmentArgs by navArgs()

    //퍼미션 확인 후 콜백 리스너
    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            goGallery()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFootprintDialogBinding.inflate(inflater, container, false)

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        //해시태그 관련 설정(TextMatcher)
        setHashTag()
        //뷰페이저 어댑터 설정
        initAdapter()
        //클릭 리스너 설정
        setMyClickListener()

        if (args.footprint!!.isNotBlank()) {    //발자국 수정
            footprint = Gson().fromJson(args.footprint, FootprintModel::class.java)
            isUpdate = true
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
            selectedUri.add(Uri.parse(it))
        }

        TedImagePicker.with(requireContext())
            .selectedUri(selectedUri)
            .buttonText(R.string.action_next)
            .buttonBackground(R.color.white)
            .buttonTextColor(R.color.primary)
            .showTitle(false)
            .backButton(R.drawable.ic_cancel)
            .savedDirectoryName("Footprint")
            .max(5, R.string.error_photo_cnt_exceeded)
            .startMultiImage { uriList ->
                imgList.clear()
                uriList.forEach {
                    imgList.add(it.toString())
                }

                photoRVAdapter.addImgList(imgList)
                binding.postDialogPhotoIndicator.setViewPager(binding.postDialogPhotoVp)

                if (uriList.isEmpty()) {    //이미지를 선택하지 않았으면
                    setDeletePhotoUI()
                } else {    //이미지를 선택했다면
                    binding.postDialogPhotoVp.visibility = View.VISIBLE
                    binding.postDialogPhotoIndicator.visibility = View.VISIBLE
                    binding.postDialogAddPhotoTv.text = getString(R.string.action_delete_photo)
                }
            }
    }

    private fun initAdapter() {
        photoRVAdapter = PhotoRVAdapter(0)
        photoRVAdapter.setMyItemClickListener(object : PhotoRVAdapter.MyItemClickListener {
            override fun goGalleryClick() {
                goGallery()
            }
        })
        binding.postDialogPhotoVp.adapter = photoRVAdapter
    }

    private fun setHashTag() {
        //해시태그 관련 설정
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
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_hashtag_exceed_cnt),
                        Toast.LENGTH_SHORT
                    ).show()


                    textMatcherFlag = 0
                    binding.postDialogContentEt.addTextChangedListener(this)
                    binding.postDialogContentEt.removeRule(hashtagRule)
                }
            }
        }
    }

    //find hashtag
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
        //취소 누르면 다이얼로그 나가기 -> 이전 화면(WalkMapFragment)에 기록데이터를 null 로 하여 데이터 전달
        binding.postDialogCancelTv.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "post",
                null
            )

            dismiss()
        }

        //갤러리 이동 전 퍼미션 체크
        binding.postDialogAddPhotoIv.setOnClickListener {
            if (binding.postDialogAddPhotoTv.text == getString(R.string.action_add_photo))
                checkPermission()
            else
                setDeletePhotoUI()
        }

        //사진 추가하기/사진 삭제하기 텍스트뷰 클릭 리스너
        binding.postDialogAddPhotoTv.setOnClickListener {
            if ((it as TextView).text == getString(R.string.action_add_photo))
                checkPermission()
            else
                setDeletePhotoUI()
        }

        //저장 텍스트뷰 클릭 리스너
        binding.postDialogSaveTv.setOnClickListener {
            if (binding.postDialogContentEt.text!!.isBlank() && imgList.isEmpty()) {  //필수 데이터(기록 내용)가 입력됐는지 확인
                //"사진이나 글을 추가해주세요." 다이얼로그 화면 띄우기
                val action =
                    FootprintDialogFragmentDirections.actionGlobalMsgDialogFragment(
                        getString(R.string.msg_add_photo_or_writing)
                    )
                findNavController().navigate(action)
            } else {
                dismiss()   //프래그먼트 종료

                if (isUpdate) { //발자국 추가하기 -> 지금까지 입력한 발자국 데이터를 이전 화면(WalkMapFragment)으로 전달
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "updatedFootprint",
                        Gson().toJson(setFootprintData())
                    )
                } else {    //발자국 수정하기 -> 수정된 발자국 데이터를 이전 화면(WalkMapFragment)으로 전달
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "footprint",
                        Gson().toJson(setFootprintData())
                    )
                }

                //"발자국을 남겼어요." 다이얼로그 화면 띄우기
                val action =
                    FootprintDialogFragmentDirections.actionGlobalMsgDialogFragment(
                        getString(R.string.msg_leave_footprint)
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun setFootprintData(): FootprintModel {
        var hashtags = findHashTag()
        if (hashtags.isNotEmpty() && hashtags.size > 5)
            hashtags = hashtags.slice(0..4) as java.util.ArrayList<String>

        if (!::footprint.isInitialized) {
            footprint = FootprintModel()

            val current = LocalDateTime.now(TimeZone.getTimeZone("Asia/Seoul").toZoneId())
            footprint.recordAt = current.format(DateTimeFormatter.ISO_DATE_TIME)
        }
        footprint.content = binding.postDialogContentEt.text.toString()
        footprint.hashtagList = hashtags
        footprint.photos = imgList

        return footprint
    }

    private fun setDeletePhotoUI() {
        binding.postDialogPhotoVp.visibility = View.GONE
        binding.postDialogPhotoIndicator.visibility = View.GONE
        binding.postDialogAddPhotoTv.text = getString(R.string.action_add_photo)
        imgList.clear()
        photoRVAdapter.clearImgList()
    }

    private fun setUI(footprint: FootprintModel) {
        binding.postDialogContentEt.setText(footprint.content)

        if (footprint.photos.isEmpty()) {
            setDeletePhotoUI()
        } else {
            imgList.clear()
            imgList.addAll(footprint.photos)

            binding.postDialogPhotoVp.visibility = View.VISIBLE
            photoRVAdapter.addImgList(footprint.photos as ArrayList<String>)

            binding.postDialogPhotoIndicator.visibility = View.VISIBLE
            binding.postDialogPhotoIndicator.setViewPager(binding.postDialogPhotoVp)

            binding.postDialogAddPhotoTv.setText(R.string.action_delete_photo)
        }
    }
}