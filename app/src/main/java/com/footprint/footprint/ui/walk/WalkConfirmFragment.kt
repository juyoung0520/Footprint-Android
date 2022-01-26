package com.footprint.footprint.ui.walk

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.footprint.footprint.R
import com.footprint.footprint.data.model.FootprintModel
import com.footprint.footprint.databinding.FragmentWalkConfirmBinding
import com.footprint.footprint.data.model.FootprintsModel
import com.footprint.footprint.ui.BaseFragment
import com.footprint.footprint.ui.adapter.FootprintRVAdapter
import com.footprint.footprint.ui.dialog.ActionDialogFragment
import com.footprint.footprint.ui.main.calendar.WalkDetailActivity
import com.google.gson.Gson
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class WalkConfirmFragment :
    BaseFragment<FragmentWalkConfirmBinding>(FragmentWalkConfirmBinding::inflate) {
    private lateinit var actionDialogFragment: ActionDialogFragment
    private lateinit var footprints: FootprintsModel
    private lateinit var footprintRVAdapter: FootprintRVAdapter

    private val args by navArgs<WalkConfirmFragmentArgs>()

    //뒤로가기 콜백
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            binding.walkConfirmSlidingUpPanelLayout.apply {
                if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED || panelState == SlidingUpPanelLayout.PanelState.ANCHORED)   //SlidingUpPanelLayout 이 위로 올라가 있으면 아래로 내리기
                    panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                else if (requireActivity() is WalkDetailActivity) { //WalkDetailActivity 화면에서 온 경우 -> 액티비티 종료
                    requireActivity().finish()
                } else {    //WalkAfterActivity 화면에서 온 경우 -> ‘OO번째 산책’ 작성을 취소할까요? 다이얼로그 화면 띄우기
                    setWalkDialogBundle(getString(R.string.msg_stop_walk))
                    actionDialogFragment.show(
                        requireActivity().supportFragmentManager,
                        null
                    )
                }
            }
        }

    }

    private var position: Int = -1  //클릭된 기록 인덱스

    override fun initAfterBinding() {
        //이전 화면(WalkAfterActivity or WalkDetailActivity)에서 전달받은 기록 데이터(footprints)를 FootprintsModel 로 변환
        val footprintsStr = args.footprints
        if (footprintsStr.isNotBlank())  //footprintsStr 이 비어 있다는 건 발자국 데이터가 없다는 뜻
            footprints = Gson().fromJson(footprintsStr, FootprintsModel::class.java)

        //어댑터 초기화는 한번만
        if (!::footprintRVAdapter.isInitialized)
            initAdapter()

        setWalkDialog()
        setFootprintView()
        observe()

        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)  //뒤로가기 콜백 리스너 등록
    }

    //발자국 기록을 보여주는 뷰 설정
    private fun setFootprintView() {
        if (requireActivity() is WalkDetailActivity && !::footprints.isInitialized) { //WalkDetailActivity 에서 기록이 없을 경우 -> 산책 기록이 없어요! 텍스트뷰 보여주기
            binding.walkConfirmNoFootprintTv.visibility = View.VISIBLE
            binding.walkConfirmSlidedLayout.visibility = View.INVISIBLE
        } else {    //그 이외에 모든 경우 -> slidedPanelLayout 보여주기
            binding.walkConfirmNoFootprintTv.visibility = View.INVISIBLE
            binding.walkConfirmSlidedLayout.visibility = View.VISIBLE

            val heightPixels = resources.displayMetrics.heightPixels
            binding.walkConfirmSlidingUpPanelLayout.panelHeight = (heightPixels * 0.5).toInt()
        }
    }

    private fun observe() {
        //실시간 글 작성하기 화면으로부터 전달 받는 post 데이터
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("post")
            ?.observe(viewLifecycleOwner) {
                Log.d("WalkConfirmFragment", "post observe -> $it")

                if (it != null) {
                    //string -> FootprintModel
                    val footprint = Gson().fromJson<FootprintModel>(it, FootprintModel::class.java)

                    if (footprint.isUpdate) {
                        //어댑터에 데이터 수정하고 UI 업데이트
                        footprintRVAdapter.updateData(footprint, position)
                    } else {
                        //어댑터에 데이터 추가하고 UI 업데이트
                        footprintRVAdapter.addData(footprint, position + 1)
                    }
                }
            }
    }

    //기록 관련 리사이클러뷰 초기화
    private fun initAdapter() {
        footprintRVAdapter = FootprintRVAdapter(requireActivity().javaClass.simpleName)
        footprintRVAdapter.setMyItemClickListener(object : FootprintRVAdapter.MyItemClickListener {
            //발자국 삭제 텍스트뷰 클릭 리스너 -> 해당 발자국을 삭제할까요? 다이얼로그 화면 띄우기
            override fun showDeleteDialog(position: Int) {
                this@WalkConfirmFragment.position = position  //클릭된 post 인덱스를 전역변수로 저장해 놓는다.

                setWalkDialogBundle(getString(R.string.msg_delete_footprint))
                actionDialogFragment.show(requireActivity().supportFragmentManager, null)
            }

            //발자국 추가 텍스트뷰 클릭 리스너
            override fun addFootprint(position: Int) {
                this@WalkConfirmFragment.position = position

                if (footprints.footprints.size >= 9) {
                    //"발자국은 최대 9개까지 남길 수 있어요." 다이얼로그 화면 띄우기
                    val action =
                        WalkConfirmFragmentDirections.actionGlobalMsgDialogFragment(
                            getString(R.string.error_post_cnt_exceed)
                        )
                    findNavController().navigate(action)
                } else {
                    //발자국 작성하기 다이얼로그 화면 띄우기
                    val action =
                        WalkConfirmFragmentDirections.actionWalkConfirmFragment2ToFootprintDialogFragment3(
                            ""
                        )
                    findNavController().navigate(action)
                }
            }

            //발자국 편집 텍스트뷰 클릭 리스너
            override fun updateFootprint(position: Int, footprint: FootprintModel) {
                this@WalkConfirmFragment.position = position

                footprint.isUpdate = true   //수정하는 데이터임을 알리기
                //발자국 작성하기 다이얼로그 화면 띄우기(수정)
                val action =
                    WalkConfirmFragmentDirections.actionWalkConfirmFragment2ToFootprintDialogFragment3(
                        Gson().toJson(footprint)
                    )
                findNavController().navigate(action)
            }
        })

        //어댑터에 데이터 저장
        if (::footprints.isInitialized) {
            footprintRVAdapter.setData(footprints)
        }
        binding.walkConfirmPostRv.adapter = footprintRVAdapter
    }

    //WalkDialogFragment 초기화
    private fun setWalkDialog() {
        actionDialogFragment = ActionDialogFragment()

        actionDialogFragment.setMyDialogCallback(object : ActionDialogFragment.MyDialogCallback {
            override fun finish(isFinished: Boolean) {
                if (isFinished)
                    requireActivity().finish()
            }

            override fun save(isSaved: Boolean) {
            }

            override fun delete(isDelete: Boolean) {
                if (isDelete)
                    footprintRVAdapter.removeData(this@WalkConfirmFragment.position)
            }
        })
    }

    //WalkDialogFragment 에 넘겨준 메세지(ex.‘OO번째 산책’을 저장할까요?)를 저장하는 함수
    private fun setWalkDialogBundle(msg: String) {
        val bundle: Bundle = Bundle()
        bundle.putString("msg", msg)

        actionDialogFragment.arguments = bundle
    }
}