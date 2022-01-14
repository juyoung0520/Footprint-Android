package com.footprint.footprint.ui.post

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentPostDialogBinding
import com.footprint.footprint.utils.DialogFragmentUtils
import gun0912.tedimagepicker.builder.TedImagePicker
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.santalu.textmatcher.rule.HashtagRule
import com.santalu.textmatcher.style.MentionStyle

class PostDialogFragment : DialogFragment(), TextWatcher {
    private lateinit var binding: FragmentPostDialogBinding
    private lateinit var photoRVAdapter: PhotoRVAdapter

    private var textMatcherFlag: Int = 1

    private val imgList: ArrayList<Uri> = arrayListOf<Uri>()  //선택한 사진을 저장하는 변수(추후에 사용될 듯)

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

        //취소 누르면 다이얼로그 나가기
        binding.postDialogCancelTv.setOnClickListener {
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
            val hashtags = findHashTag()
            Log.d("PostDialogFragment", "저장! -> 해시태그: ${hashtags.subList(0, 5)}")
        }

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
        TedImagePicker.with(requireContext())
            .selectedUri(imgList)
            .buttonText(R.string.action_next)
            .buttonBackground(R.color.white)
            .buttonTextColor(R.color.primary)
            .showTitle(false)
            .backButton(R.drawable.ic_cancel)
            .max(5, R.string.error_photo_cnt_exceeded)
            .startMultiImage { uriList ->
                imgList.clear()
                imgList.addAll(uriList)

                photoRVAdapter.addData(imgList)
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
        photoRVAdapter = PhotoRVAdapter()
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
            textColor = R.color.primary_dark,
            isBold = false
        )
        var hashtagRule = HashtagRule(allowedCharacters = "-", style=mentionStyle)
        binding.postDialogContentEt.addRule(hashtagRule)

        binding.postDialogContentEt.setOnMatchListener { rule, s ->
            if (s=="#") {
                //현재 EditText 에 작성된 해시태그의 개수를 찾기 위한 코드
                val hashtags = findHashTag()

                if (hashtags.size>5) {
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
    private fun findHashTag(): ArrayList<String> {
        val pattern = Regex("#([0-9a-zA-Z가-힣]*)")
        val matchedWords = pattern.findAll(binding.postDialogContentEt.text.toString())
        val hashtags: ArrayList<String> = arrayListOf()

        matchedWords.forEach {
            hashtags.add(it.value)
        }

        return hashtags
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