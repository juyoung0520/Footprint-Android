package com.footprint.footprint.ui.setting

import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.databinding.FragmentNoticeDetailBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.viewmodel.NoticeDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NoticeDetailFragment : BaseFragment<FragmentNoticeDetailBinding>(FragmentNoticeDetailBinding::inflate) {
    private val args: NoticeDetailFragmentArgs by navArgs()
    private val noticeDetailVm: NoticeDetailViewModel by viewModel()

    private var index = args.idx.toInt()
    private var prevIdx: Int? = null
    private var nextIdx: Int? = null

    override fun onResume() {
        super.onResume()

        // 로딩바 띄우기

        /* 테스트 - getNotice */
        //noticeDetailVm.getNotice(index)
    }

    override fun initAfterBinding() {
        observe()
        setMyClickListener()
    }

    private fun setMyClickListener(){
        binding.noticePrevLayout.setOnClickListener {
            if(prevIdx != null){
                noticeDetailVm.getNotice(prevIdx!!)
            }
        }

        binding.noticeNextLayout.setOnClickListener {
            if(nextIdx != null){
                noticeDetailVm.getNotice(nextIdx!!)
            }
        }
    }

    private fun observe(){
        noticeDetailVm.mutableErrorType.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

        })

        noticeDetailVm.notice.observe(this, Observer{
            index = it.noticeIdx
            prevIdx = it.prevIdx
            nextIdx = it.nextIdx

            bind(it)

            // 로딩바 지우기
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
        if(notice.prevIdx == null)
            binding.noticePrevLayout.alpha = 0.3F
        else
            binding.noticePrevLayout.alpha = 1F

        // 다음 글 있는지 체크
        if(notice.nextIdx == null)
            binding.noticeNextLayout.alpha = 0.3F
        else
            binding.noticeNextLayout.alpha = 1F

    }

}