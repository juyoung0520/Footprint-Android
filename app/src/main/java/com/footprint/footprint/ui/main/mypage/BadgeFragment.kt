package com.footprint.footprint.ui.main.mypage

import android.os.Bundle
import com.footprint.footprint.R
import com.footprint.footprint.data.remote.badge.Badge
import com.footprint.footprint.data.remote.badge.BadgeResponse
import com.footprint.footprint.databinding.FragmentBadgeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.BadgeRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceWidth

class BadgeFragment : BaseFragment<FragmentBadgeBinding>(FragmentBadgeBinding::inflate) {

    private lateinit var badgeRVAdapter: BadgeRVAdapter
    private lateinit var actionDialogFragment: ActionDialogFragment

    //대표 뱃지를 변경할 때 잠깐 변경할 대표 뱃지 데이터를 담아 놓는 전역 변수(임시)
    private var representativeBadge: Badge? = null

    //뱃지 데이터(임시)
    private val badgeRes = BadgeResponse(
        badges = listOf(
            Badge(
                "10km",
                R.drawable.ic_badge_10km,
                0
            ),
            Badge(
                "누적기록 10회",
                R.drawable.ic_footprint_10,
                4
            ),
            Badge(
                "22.01 PRO",
                R.drawable.ic_badge_202201_pro,
                7
            )
        ),
        representativeBadge = Badge(
            "22.01 PRO",
            R.drawable.ic_badge_202201_pro,
            7
        )
    )

    override fun initAfterBinding() {
        bindRepresentativeBade(badgeRes.representativeBadge)

        if (!::badgeRVAdapter.isInitialized)
            initAdapter()

        if (!::actionDialogFragment.isInitialized)
            initActionDialog()

        setMyClickListener()
    }

    //대표 뱃지 데이터 바인딩
    private fun bindRepresentativeBade(badge: Badge) {
        binding.badgeRepresentativeBadgeIv.setImageResource(badge.img)
        binding.badgeRepresentativeBadgeNameTv.text = badge.name
    }

    private fun initAdapter() {
        //디바이스 크기에 맞춰 뱃지 아이템의 크기 조정하기
        val size = (getDeviceWidth() - convertDpToPx(requireContext(), 74)) / 3

        badgeRVAdapter = BadgeRVAdapter(badgeRes.representativeBadge, size)
        badgeRVAdapter.setData(badgeRes.badges)

        //대표뱃지 변경 클릭 리스너
        badgeRVAdapter.setMyItemClickListener(object : BadgeRVAdapter.MyItemClickListener {
            override fun changeRepresentativeBadge(badge: Badge) {
                representativeBadge = badge

                val bundle = Bundle()
                bundle.putSerializable("msg", getString(R.string.msg_change_representative_badge))
                actionDialogFragment.arguments = bundle

                actionDialogFragment.show(requireActivity().supportFragmentManager, null)
            }

        })

        binding.badgeBadgeRv.adapter = badgeRVAdapter
    }

    private fun initActionDialog() {
        actionDialogFragment = ActionDialogFragment()

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun action1(isAction: Boolean) {
                if (isAction) {
                    badgeRes.representativeBadge = representativeBadge!!
                    bindRepresentativeBade(representativeBadge!!)

                    //어댑터에도 대표뱃지를 변경하는 메서드 호출
                    badgeRVAdapter.changeRepresentativeBadge(representativeBadge!!)
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

        binding.badgeAchievementBadgeTv.setOnClickListener {
            badgeRVAdapter.addBadge(
                Badge(
                    "누적기록 30회",
                    R.drawable.ic_footprint_30,
                    6
                )
            )
        }
    }
}