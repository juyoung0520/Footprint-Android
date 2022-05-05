package com.footprint.footprint.ui.setting

import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.footprint.footprint.data.dto.NoticeDto
import com.footprint.footprint.databinding.FragmentNoticeDetailBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.viewmodel.NoticeDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NoticeDetailFragment : BaseFragment<FragmentNoticeDetailBinding>(FragmentNoticeDetailBinding::inflate) {
    private val args: NoticeDetailFragmentArgs by navArgs()
    private val noticeDetailVm: NoticeDetailViewModel by viewModel()

    private var index = args.idx.toInt()
    private var max = 1

    override fun initAfterBinding() {
        noticeDetailVm.getNotice(index)

        observe()
        setMyClickListener()
    }

    private fun setMyClickListener(){
        binding.noticePrevLayout.setOnClickListener {
            if(index != 1){
                noticeDetailVm.getNotice(index - 1)
            }
        }

        binding.noticeNextLayout.setOnClickListener {
            if(max != 1){
                noticeDetailVm.getNotice(index + 1)
            }
        }
    }

    private fun observe(){
        noticeDetailVm.mutableErrorType.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

        })

        noticeDetailVm.notice.observe(this, Observer{
            index = it.noticeIdx
            bind(it)
        })
    }

    private fun bind(notice: NoticeDto){
        binding.noticeDetailDateTv.text = notice.updateAt.toString()
        binding.noticeDetailTitleTv.text = notice.title

        when(index){ // 현재 idx에 따라 이전글, 다음글 텍스트 활성/비활성화
            1 -> {
                binding.noticePrevLayout.alpha = 0.3F
                binding.noticeNextLayout.alpha = 1F
            }
            max -> {
                binding.noticePrevLayout.alpha = 1F
                binding.noticeNextLayout.alpha = 0.3F
            }
            else -> {
                binding.noticePrevLayout.alpha = 1F
                binding.noticeNextLayout.alpha = 1F
            }
        }
    }

}