package com.footprint.footprint.ui.setting

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentNoticeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.NoticeRVAdapter
import com.footprint.footprint.utils.convertPxToDp
import com.footprint.footprint.utils.getDeviceWidth
import com.footprint.footprint.viewmodel.NoticeListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NoticeFragment : BaseFragment<FragmentNoticeBinding>(FragmentNoticeBinding::inflate) {

    private val noticeListVm: NoticeListViewModel by viewModel()
    private lateinit var noticeRVAdapter: NoticeRVAdapter

    private var max = 1
    private var current = 1
    private var size = 1

    override fun onResume() {
        super.onResume()

        // 로딩바 띄우기

        /* 테스트 - getNoticeList */
        //noticeListVm.getNoticeList(1, size)
    }

    override fun initAfterBinding() {

        val heightDp = convertPxToDp(requireContext(), getDeviceWidth()) - (90 + 54)
        size = heightDp / 64

        Log.d("Notice", "size는 $size")

        observe()

        noticeRVAdapter = NoticeRVAdapter()
        binding.noticeRv.adapter = noticeRVAdapter

        /*테스트입니다*/
        max = 2
        initPageIndicator(max)
        current = 2
        bind(current)

        setMyEventListener()
    }

    private fun setMyEventListener() {
        //뒤로가기 이미지뷰 클릭 리스너 -> 프래그먼트 종료
        binding.noticeBackIv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // 1, 2, 3 각 페이지 버튼 누르면 -> 해당하는 페이지 정보 요청
        binding.noticePage1Tv.setOnClickListener {
            updatePage(binding.noticePage1Tv.text.toString().toInt())
        }

        binding.noticePage2Tv.setOnClickListener {
            updatePage(binding.noticePage2Tv.text.toString().toInt())
        }

        binding.noticePage3Tv.setOnClickListener {
            updatePage(binding.noticePage3Tv.text.toString().toInt())
        }

        // 처음, 마지막 페이지 버튼 누르면 -> 처음, 마지막 페이지 정보 요청
        binding.noticeFirstIv.setOnClickListener {
            if(current != 1){
                updatePage(1)
            }
        }

        binding.noticeLastIv.setOnClickListener {
            if(current != max){
                updatePage(max)
            }
        }

        // 클릭 이벤트 발생 시
        noticeRVAdapter.setMyItemClickListener(object : NoticeRVAdapter.MyItemClickListener{

            override fun showNoticeDetail(idx: Int) {

                // Item 클릭 시, notice idx 정보 가지고 detail 화면으로 이동
                val action = NoticeFragmentDirections.actionNoticeFragmentToNoticeDetailFragment(idx.toString())
                findNavController().navigate(action)
            }

        })

    }

    private fun initPageIndicator(total: Int){
        when(total){
            1 -> { // 총 페이지가 1개
                binding.noticePage1Tv.visibility = View.VISIBLE
                binding.noticePage2Tv.visibility = View.GONE
                binding.noticePage3Tv.visibility = View.GONE
            }
            2 -> { // 총 페이지가 2개
                binding.noticePage1Tv.visibility = View.VISIBLE
                binding.noticePage2Tv.visibility = View.VISIBLE
                binding.noticePage3Tv.visibility = View.GONE
            }
            else -> { // 그 이상
                binding.noticePage1Tv.visibility = View.VISIBLE
                binding.noticePage2Tv.visibility = View.VISIBLE
                binding.noticePage3Tv.visibility = View.VISIBLE
            }
        }
    }

    private fun bind(current: Int){
        setPageIndicator(current)
        setPageNum(current)
    }

    private fun setPageIndicator(current: Int){
        if(max == 1){
            binding.noticeFirstIv.alpha = 0.3F
            binding.noticeLastIv.alpha = 0.3F
        }else if(max >= 2){
            binding.noticeFirstIv.alpha = 1F
            binding.noticeLastIv.alpha = 1F

            if(current == 1){ // 첫 페이지
                binding.noticeFirstIv.alpha = 0.3F
            }else if (current == max){ // 마지막 페이지
                binding.noticeLastIv.alpha = 0.3F
            }
        }
    }

    private fun setPageNum(current: Int){
        var num: Int

        binding.noticePage1Tv.setTextColor(resources.getColor(R.color.black_light))
        binding.noticePage1Tv.alpha = 0.3F
        binding.noticePage2Tv.setTextColor(resources.getColor(R.color.black_light))
        binding.noticePage2Tv.alpha = 0.3F
        binding.noticePage3Tv.setTextColor(resources.getColor(R.color.black_light))
        binding.noticePage3Tv.alpha = 0.3F

        when(current){
            1 -> {
                // 첫 페이지면, 1에 적용
                binding.noticePage1Tv.text = current.toString()
                binding.noticePage1Tv.setTextColor(resources.getColor(R.color.black))
                binding.noticePage1Tv.alpha = 1F

                num = current + 1
                binding.noticePage2Tv.text = num.toString()
                num = current + 2
                binding.noticePage3Tv.text = num.toString()

                return
            }
            max -> {
                if(max != 2){  // 마지막 페이지면, 3에 적용
                    binding.noticePage3Tv.text = current.toString()
                    binding.noticePage3Tv.setTextColor(resources.getColor(R.color.black))
                    binding.noticePage3Tv.alpha = 1F

                    num = current - 2
                    binding.noticePage1Tv.text = num.toString()
                    num = current - 1
                    binding.noticePage2Tv.text = num.toString()
                    return
                }
            }
        }

        binding.noticePage2Tv.text = current.toString()
        binding.noticePage2Tv.setTextColor(resources.getColor(R.color.black))
        binding.noticePage2Tv.alpha = 1F

        num = current - 1
        binding.noticePage1Tv.text = num.toString()
        num = current + 1
        binding.noticePage3Tv.text = num.toString()
    }

    private fun updatePage(page: Int){
        noticeListVm.getNoticeList(page, size)

        // 로딩 띄우기
    }

    private fun observe(){
        noticeListVm.mutableErrorType.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

        })

        noticeListVm.noticeList.observe(this, Observer{
            if(it.size > 0){
                // 응답 오면 로딩바 지우고 update
                noticeRVAdapter.updateNoticeList(it)
                Log.d("Notice", "Notice List = $it")
            }
        })

        noticeListVm.totalPage.observe(this, Observer {
            max = it
            initPageIndicator(it)
        })

        noticeListVm.currentPage.observe(this, Observer {
            current = it
            bind(it)
        })
    }
}