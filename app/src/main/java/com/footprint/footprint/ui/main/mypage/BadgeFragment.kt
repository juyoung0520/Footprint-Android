package com.footprint.footprint.ui.main.mypage

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentBadgeBinding
import com.footprint.footprint.domain.model.Badge
import com.footprint.footprint.domain.model.RepresentativeBadge
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.BadgeRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.utils.*
import com.footprint.footprint.viewmodel.BadgeViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class BadgeFragment : BaseFragment<FragmentBadgeBinding>(FragmentBadgeBinding::inflate) {

    private lateinit var badgeRVAdapter: BadgeRVAdapter
    private lateinit var actionDialogFragment: ActionDialogFragment
    private lateinit var networkErrSbGet: Snackbar
    private lateinit var networkErrSbPatch: Snackbar

    private var representativeBadgeIdx: Int? = null //대표 뱃지를 변경할 때 잠깐 변경할 대표 뱃지 인덱스를 담아 놓는 전역 변수

    private val badgeVm: BadgeViewModel by viewModel()

    override fun initAfterBinding() {
        setMyClickListener()
        observe()

        if (!::actionDialogFragment.isInitialized)
            initActionDialog()

        badgeVm.getBadges()
        binding.badgeLoadingPb.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()

        if (::networkErrSbGet.isInitialized && networkErrSbGet.isShown)
            networkErrSbGet.dismiss()
        else if (::networkErrSbPatch.isInitialized && networkErrSbPatch.isShown)
            networkErrSbPatch.dismiss()
    }

    private fun initAdapter(badgeInfo: com.footprint.footprint.domain.model.BadgeInfo) {
        //디바이스 크기에 맞춰 뱃지 아이템의 크기 조정하기
        val size = (getDeviceWidth() - convertDpToPx(requireContext(), 74)) / 3

        badgeRVAdapter = BadgeRVAdapter(badgeInfo.repBadgeInfo, size) //어댑터에 대표 뱃지 정보와 뱃지 크기를 전달한다.
        badgeRVAdapter.setData(badgeInfo.badgeList)

        //대표뱃지 변경 클릭 리스너
        badgeRVAdapter.setMyItemClickListener(object : BadgeRVAdapter.MyItemClickListener {
            override fun changeRepresentativeBadge(badge: Badge) {
                representativeBadgeIdx = badge.badgeIdx //임시로 사용자가 선택한 대표뱃지 인덱스를 저장

                //대표뱃지로 설정할까요? 다이얼로그 띄우기
                val bundle = Bundle()
                bundle.putSerializable("msg", getString(R.string.msg_change_representative_badge))
                bundle.putString("left", getString(R.string.action_cancel))
                bundle.putString("right", getString(R.string.action_set))

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
            override fun leftAction(action: String) {
            }

            override fun rightAction(action: String) {
                badgeVm.changeRepresentativeBadge(representativeBadgeIdx!!)
            }
        })
    }

    private fun setMyClickListener() {
        //뒤로가기 버튼 클릭 리스너 ->프래그먼트 종료
        binding.badgeBackIv.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    //대표 뱃지 데이터 바인딩
    private fun bindRepresentativeBade(badge: RepresentativeBadge) {
        if (badge.repBadgeInfo.badgeIdx==0) { //얻은 뱃지가 없어서 대표 뱃지가 없을 때
            binding.badgeRepresentativeBadgeIv.visibility = View.INVISIBLE
            binding.badgeRepresentativeBadgeNameTv.visibility = View.INVISIBLE
        } else {    //대표 뱃지가 있을 때
            binding.badgeRepresentativeBadgeIv.visibility = View.VISIBLE
            binding.badgeRepresentativeBadgeIv.loadSvg(requireContext(), badge.repBadgeInfo.badgeUrl)
            binding.badgeRepresentativeBadgeNameTv.visibility = View.VISIBLE
            binding.badgeRepresentativeBadgeNameTv.text = badge.repBadgeInfo.badgeName
        }
    }

    private fun observe() {
        badgeVm.mutableErrorType.observe(viewLifecycleOwner, Observer {
            binding.badgeLoadingPb.visibility = View.INVISIBLE

            when (it) {
                ErrorType.NETWORK -> {
                    when (badgeVm.getErrorMethod()) {
                        "getBadges" -> {
                            networkErrSbGet = Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) { badgeVm.getBadges() }
                            networkErrSbGet.show()
                        }
                        "changeRepresentativeBadge" -> {
                            networkErrSbPatch = Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.action_retry)) { badgeVm.changeRepresentativeBadge(representativeBadgeIdx!!) }
                            networkErrSbPatch.show()
                        }
                    }
                }
                ErrorType.UNKNOWN, ErrorType.DB_SERVER -> {
                    startErrorActivity("BadgeFragment")
                }
            }
        })

        badgeVm.badges.observe(viewLifecycleOwner, Observer {
            LogUtils.d("BadgeFragment", "badges Observe!! -> $it")

            binding.badgeLoadingPb.visibility = View.INVISIBLE //로딩 프로그래스바 INVISIBLE
            bindRepresentativeBade(it.repBadgeInfo) //변경된 대표뱃지로 UI 업데이트
            initAdapter(it)
        })

        badgeVm.representativeBadge.observe(viewLifecycleOwner, Observer {
            bindRepresentativeBade(it) //변경된 대표뱃지로 UI 업데이트
            badgeRVAdapter.changeRepresentativeBadge(it)   //어댑터에도 대표뱃지를 변경하는 메서드 호출
        })
    }
}