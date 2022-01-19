package com.footprint.footprint.ui.dialog

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentPostDialogBinding
import com.footprint.footprint.model.PostModel
import com.footprint.footprint.ui.adapter.PhotoRVAdapter
import com.footprint.footprint.utils.DialogFragmentUtils
import com.google.gson.Gson
import gun0912.tedimagepicker.builder.TedImagePicker
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.santalu.textmatcher.rule.HashtagRule
import com.santalu.textmatcher.style.MentionStyle
import java.util.*
import kotlin.collections.ArrayList

class PostDialogFragment() : DialogFragment(), TextWatcher {
    private lateinit var binding: FragmentPostDialogBinding
    private lateinit var photoRVAdapter: PhotoRVAdapter

    private var textMatcherFlag: Int = 1

    private val imgList: ArrayList<String> = arrayListOf<String>()  //선택한 사진을 저장하는 변수

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
        binding = FragmentPostDialogBinding.inflate(inflater, container, false)

        //다이얼로그 프래그먼트 모서리 둥글게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        //해시태그 관련 설정(TextMatcher)
        setHashTag()
        //뷰페이저 어댑터 설정
        initAdapter()
        //클릭 리스너 설정
        setMyClickListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        //전체 프래그먼트 크기 설정
        DialogFragmentUtils.dialogFragmentResize(
            requireContext(),
            this@PostDialogFragment,
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

                if (uriList.isEmpty()) {
                    binding.postDialogPhotoVp.visibility = View.GONE
                    binding.postDialogPhotoIndicator.visibility = View.GONE
                    binding.postDialogAddPhotoIv.visibility = View.VISIBLE
                    binding.postDialogAddPhotoTv.visibility = View.VISIBLE
                } else {
                    binding.postDialogPhotoVp.visibility = View.VISIBLE
                    binding.postDialogPhotoIndicator.visibility = View.VISIBLE
                    binding.postDialogAddPhotoIv.visibility = View.GONE
                    binding.postDialogAddPhotoTv.visibility = View.GONE
                }
                /*
                //추후에 사용될 듯
                for (uri in uriList) {
                    imgList.add(getAbsolutePathByBitmap(uriToBitmap(uri)))
                }*/
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
        var mentionStyle = MentionStyle(
            textColor = R.color.primary,
            isBold = false
        )
        var hashtagRule = HashtagRule(allowedCharacters = "-", style = mentionStyle)
        binding.postDialogContentEt.addRule(hashtagRule)

        binding.postDialogContentEt.setOnMatchListener { rule, s ->
            if (s == "#") {
                //현재 EditText 에 작성된 해시태그의 개수를 찾기 위한 코드
                val hashtags = findHashTag()

                if (hashtags.size > 5) {
                    Toast.makeText(requireContext(), "해시 태그는 5개까지 작성할 수 있습니다.", Toast.LENGTH_SHORT)
                        .show()
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
            findNavController().getBackStackEntry(R.id.walkMapFragment)?.savedStateHandle?.set(
                "post",
                null
            )
            dismiss()
        }

        //갤러리 이동 전 퍼미션 체크
        binding.postDialogAddPhotoIv.setOnClickListener {
            checkPermission()
        }
        binding.postDialogAddPhotoTv.setOnClickListener {
            checkPermission()
        }

        //저장 텍스트뷰 클릭 리스너
        binding.postDialogSaveTv.setOnClickListener {
            if (binding.postDialogContentEt.text!!.isBlank())   //필수 데이터(기록 내용)가 입력됐는지 확인
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_blank_content),
                    Toast.LENGTH_SHORT
                ).show()
            else {
                //토스트 메세지 띄우기
                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_save_post),
                    Toast.LENGTH_SHORT
                ).show()

                //지금까지 입력한 기록 데이터를 이전 화면(WalkMapFragment)으로 전달
                findNavController().getBackStackEntry(R.id.walkMapFragment)?.savedStateHandle?.set(
                    "post",
                    Gson().toJson(setPostData())    //setPostData(): 지금까지 입력한 내용을 PostModel 에 바인딩
                )
                dismiss()
            }
        }
    }

    private fun setPostData(): PostModel {
        var hashtags = findHashTag()
        if (hashtags.isNotEmpty() && hashtags.size > 5)
            hashtags = hashtags.slice(0 .. 4) as java.util.ArrayList<String>

        return PostModel(
            content = binding.postDialogContentEt.text.toString(),
            photos = imgList,
            time = SimpleDateFormat("HH:mm").format(System.currentTimeMillis()),
            hashTags = hashtags
        )
    }

    /*//추후에 사용될 듯
    private fun uriToBitmap(uri: Uri): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                uri
            )
        } else {
            val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }

        return bitmap
    }

    private fun getAbsolutePathByBitmap(bitmap: Bitmap): String {
        val path =
            (requireContext().applicationInfo.dataDir + File.separator + System.currentTimeMillis())
        val file = File(path)
        var out: OutputStream? = null

        try {
            file.createNewFile()
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out)
        } finally {
            out?.close()
        }

        return file.absolutePath
    }*/
}