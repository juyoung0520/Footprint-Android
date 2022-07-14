package com.footprint.footprint.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.footprint.footprint.R
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.databinding.FragmentNoticeDetailBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.error.ErrorActivity
import com.footprint.footprint.utils.ErrorType
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.viewmodel.NoticeDetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class NoticeDetailFragment : BaseFragment<FragmentNoticeDetailBinding>(FragmentNoticeDetailBinding::inflate) {
    private val args: NoticeDetailFragmentArgs by navArgs()

    private val noticeDetailVm: NoticeDetailViewModel by viewModel()
    private lateinit var networkErrSb: Snackbar
    private lateinit var getResult: ActivityResultLauncher<Intent>

    private var index: Int? = null
    private var preIdx: Int = -1
    private var postIdx: Int = -1

    override fun onResume() {
        super.onResume()

        // 로딩바 띄우기
        binding.noticeDetailLoadingBgV.visibility = View.VISIBLE
        binding.noticeDetailLoadingPb.visibility = View.VISIBLE

        index = args.idx.toInt()
        noticeDetailVm.getNotice(index!!)
    }

    override fun initAfterBinding() {
        observe()
        setMyClickListener()
    }

    private fun setMyClickListener(){
        binding.noticeBackIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.noticePrevLayout.setOnClickListener {
            if(preIdx != -1){
                // 로딩바 띄우기
                binding.noticeDetailLoadingBgV.visibility = View.VISIBLE
                binding.noticeDetailLoadingPb.visibility = View.VISIBLE

                noticeDetailVm.getNotice(preIdx)
            }
        }

        binding.noticeNextLayout.setOnClickListener {
            if(postIdx != -1){
                // 로딩바 띄우기
                binding.noticeDetailLoadingBgV.visibility = View.VISIBLE
                binding.noticeDetailLoadingPb.visibility = View.VISIBLE

                noticeDetailVm.getNotice(postIdx)
            }
        }
    }

    private fun observe(){
        noticeDetailVm.mutableErrorType.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                ErrorType.NETWORK -> {
                    networkErrSb = Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) { noticeDetailVm.getNotice(index!!)}
                    networkErrSb.show()
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> startErrorActivity(getResult, "NoticeDetailFragment")
            }
        })

        noticeDetailVm.notice.observe(this, Observer{
            // 로딩바 지우기
            binding.noticeDetailLoadingBgV.visibility = View.GONE
            binding.noticeDetailLoadingPb.visibility = View.GONE

            index = it.noticeIdx
            preIdx = it.preIdx
            postIdx = it.postIdx

            bind(it)
        })
    }

    private fun bind(notice: NoticeDto){
        binding.noticeDetailDateTv.text = notice.updateAt.toString()
        binding.noticeDetailTitleTv.text = notice.title

        if(notice.notice != null)
            binding.noticeDetailContentTv.text = notice.notice

        if(notice.image != null)
            Glide.with(this).load(notice.image).into(binding.noticeDetailImageIv)

        // 새로운 글인지 확인
        if(notice.isNewNotice)
            binding.noticeNewTv.visibility = View.VISIBLE
        else
            binding.noticeNewTv.visibility = View.GONE

        // 이전 글 있는지 체크
        if(notice.preIdx == -1)
            binding.noticePrevLayout.alpha = 0.3F
        else
            binding.noticePrevLayout.alpha = 1F

        // 다음 글 있는지 체크
        if(notice.postIdx == -1)
            binding.noticeNextLayout.alpha = 0.3F
        else
            binding.noticeNextLayout.alpha = 1F

    }

    private fun initActivityResult() {
        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if(result.resultCode == ErrorActivity.RETRY){
                noticeDetailVm.getNotice(index!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityResult()
    }

    override fun onStop() {
        super.onStop()
        if (::networkErrSb.isInitialized && networkErrSb.isShown)
            networkErrSb.dismiss()
    }
}