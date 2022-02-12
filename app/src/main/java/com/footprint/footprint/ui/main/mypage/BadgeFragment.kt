package com.footprint.footprint.ui.main.mypage

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.footprint.footprint.R
import com.footprint.footprint.data.remote.badge.BadgeInfo
import com.footprint.footprint.data.remote.badge.BadgeResponse
import com.footprint.footprint.data.remote.badge.BadgeService
import com.footprint.footprint.databinding.FragmentBadgeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.BadgeRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceWidth
import com.footprint.footprint.utils.loadSvg
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BadgeFragment : BaseFragment<FragmentBadgeBinding>(FragmentBadgeBinding::inflate), BadgeView {

    private lateinit var badgeRVAdapter: BadgeRVAdapter
    private lateinit var actionDialogFragment: ActionDialogFragment

    private var representativeBadgeIdx: Int? = null //대표 뱃지를 변경할 때 잠깐 변경할 대표 뱃지 인덱스를 담아 놓는 전역 변수

    private val jobs: ArrayList<Job> = arrayListOf()

    override fun initAfterBinding() {
        BadgeService.getBadgeInfo(this) //뱃지 정보 요청 API 실행

        if (!::actionDialogFragment.isInitialized)
            initActionDialog()

        setMyClickListener()
    }

    override fun onDestroy() {
        for (job in jobs) {
            job.cancel()
        }

        super.onDestroy()
    }

    //대표 뱃지 데이터 바인딩
    private fun bindRepresentativeBade(badge: BadgeInfo) {
        binding.badgeRepresentativeBadgeIv.loadSvg(requireContext(), badge.badgeUrl)
        binding.badgeRepresentativeBadgeNameTv.text = badge.badgeName
    }

    private fun initAdapter(badgeInfo: BadgeResponse) {
        //디바이스 크기에 맞춰 뱃지 아이템의 크기 조정하기
        val size = (getDeviceWidth() - convertDpToPx(requireContext(), 74)) / 3

        badgeRVAdapter = BadgeRVAdapter(badgeInfo.repBadgeInfo, size) //어댑터에 대표 뱃지 정보와 뱃지 크기를 전달한다.
        badgeRVAdapter.setData(badgeInfo.badgeList)

        //대표뱃지 변경 클릭 리스너
        badgeRVAdapter.setMyItemClickListener(object : BadgeRVAdapter.MyItemClickListener {
            override fun changeRepresentativeBadge(badge: BadgeInfo) {
                representativeBadgeIdx = badge.badgeIdx //임시로 사용자가 선택한 대표뱃지 인덱스를 저장

                //대표뱃지로 설정할까요? 다이얼로그 띄우기
                val bundle = Bundle()
                bundle.putSerializable("msg", getString(R.string.msg_change_representative_badge))
                bundle.putString("action", getString(R.string.action_set))
                actionDialogFragment.arguments = bundle
                actionDialogFragment.show(requireActivity().supportFragmentManager, null)
            }

        })

        binding.badgeBadgeRv.adapter = badgeRVAdapter
    }

    private fun initActionDialog() {
        actionDialogFragment = ActionDialogFragment()

        //대표뱃지로 설정할까요? 다이얼로그
        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun action1(isAction: Boolean) {
                if (isAction) { //사용자가 설정을 누르면 -> 대표뱃지 변경 요청 API 실행
                    BadgeService.changeRepresentativeBadge(this@BadgeFragment, representativeBadgeIdx!!)
                }
            }

            override fun action2(isAction: Boolean) {
            }

        })
    }

    private fun setMyClickListener() {
        //뒤로가기 버튼 클릭 리스너 ->프래그먼트 종료
        binding.badgeBackIv.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onBadgeLoading() {
        if (this != null) {
            binding.badgeLoadingPb.visibility = View.VISIBLE
        }
    }

    override fun onGetBadgeSuccess(badgeInfo: BadgeResponse) {
        if (this != null) {
            binding.badgeLoadingPb.visibility = View.INVISIBLE
            bindRepresentativeBade(badgeInfo.repBadgeInfo)    //대표 뱃지 정보 UI 바인딩
            initAdapter(badgeInfo)
        }
    }

    override fun onChangeRepresentativeBadgeSuccess(representativeBadge: BadgeInfo) {
        if (this != null) {
            jobs.add(lifecycleScope.launch {
                bindRepresentativeBade(representativeBadge) //변경된 대표뱃지로 UI 업데이트
                badgeRVAdapter.changeRepresentativeBadge(representativeBadge)   //어댑터에도 대표뱃지를 변경하는 메서드 호출
            })
        }
    }

    override fun onGetBadgeFail(code: Int?) {
        if (this != null) {
            jobs.add(lifecycleScope.launch {
                binding.badgeLoadingPb.visibility = View.INVISIBLE //로딩 프로그래스바 INVISIBLE

                when (code) {
                    6000 -> {   //네트워크 연결 문제
                        Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) {
                            BadgeService.getBadgeInfo(this@BadgeFragment)
                        }.show()
                    }

                    else -> {   //그 이외 문제
                        Snackbar.make(binding.root, getString(R.string.error_api_fail), Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) {
                            BadgeService.getBadgeInfo(this@BadgeFragment)
                        }.show()
                    }
                }
            })
        }
    }

    override fun onChangeRepresentativeBadgeFail(code: Int?, badgeIdx: Int) {
        if (this != null) {
            jobs.add(lifecycleScope.launch {
                binding.badgeLoadingPb.visibility = View.INVISIBLE //로딩 프로그래스바 INVISIBLE

                when (code) {
                    6000 -> {   //네트워크 연결 문제
                        Snackbar.make(binding.root, getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) {
                            BadgeService.changeRepresentativeBadge(this@BadgeFragment, badgeIdx)
                        }.show()
                    }

                    else -> {   //그 이외 문제
                        Snackbar.make(binding.root, getString(R.string.error_api_fail), Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) {
                            BadgeService.changeRepresentativeBadge(this@BadgeFragment, badgeIdx)
                        }.show()
                    }
                }
            })
        }
    }
}