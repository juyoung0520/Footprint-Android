package com.footprint.footprint.ui.main.mypage

import android.os.Bundle
import com.footprint.footprint.R
import com.footprint.footprint.databinding.FragmentBadgeBinding
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.BadgeRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.utils.convertDpToPx
import com.footprint.footprint.utils.getDeviceWidth

class BadgeFragment : BaseFragment<FragmentBadgeBinding>(FragmentBadgeBinding::inflate) {

    private lateinit var badgeRVAdapter: BadgeRVAdapter
    private lateinit var actionDialogFragment: ActionDialogFragment

    //대표 뱃지를 변경할 때 잠깐 변경할 대표 뱃지 이미지를 담아 놓는 전역 변수(임시)
    private var representativeBadge: Int? = null
    //대표 뱃지를 변경할 때 잠깐 변경할 대표 뱃지의 순서를 담아 놓는 전역 변수(임시)
    private var representativeBadgePosition: Int? = null

    override fun initAfterBinding() {
        if (!::badgeRVAdapter.isInitialized)
            initAdapter()

        if (!::actionDialogFragment.isInitialized)
            initActionDialog()

        setMyClickListener()
    }

    private fun initAdapter() {
        //디바이스 크기에 맞춰 뱃지 아이템의 크기 조정하기
        val size = (getDeviceWidth() - convertDpToPx(requireContext(), 74)) / 3

        badgeRVAdapter = BadgeRVAdapter(7, size)
        //뱃지 데이터(임시)
        val badges = arrayListOf<Int>(
            R.drawable.ic_badge_10km,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_footprint_10,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_badge_202201_pro,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge,
            R.drawable.ic_no_badge
        )
        badgeRVAdapter.setData(badges)

        badgeRVAdapter.setMyItemClickListener(object : BadgeRVAdapter.MyItemClickListener {
            override fun changeRepresentativeBadge(badge: Int, position: Int) {
                representativeBadge = badge
                representativeBadgePosition = position

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
                    binding.badgeRepresentativeBadgeIv.setImageResource(representativeBadge!!)
                    badgeRVAdapter.changeRepresentativeBadge(representativeBadgePosition!!)
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
}