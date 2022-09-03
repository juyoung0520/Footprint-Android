package com.footprint.footprint.ui.main.course

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import com.footprint.footprint.databinding.ActivityCourseSelectBinding
import com.footprint.footprint.ui.BaseActivity
import com.footprint.footprint.ui.adapter.WalkVerCSRVAdapter
import com.footprint.footprint.ui.adapter.viewtype.WalkVerCSVT
import com.footprint.footprint.viewmodel.CourseSelectViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseSelectActivity : BaseActivity<ActivityCourseSelectBinding>(ActivityCourseSelectBinding::inflate) {
    private val vm: CourseSelectViewModel by viewModel()
    private lateinit var adapter: WalkVerCSRVAdapter

    override fun initAfterBinding() {
        initAdapter()
        setMyEventListener()
        observe()

        //나의 산책 정보 조회 API 호출
        vm.getSelfCourses()
    }

    private fun initAdapter() {
        adapter = WalkVerCSRVAdapter()
        adapter.setMyClickListener(object : WalkVerCSRVAdapter.MyClickListener {
            //각 산책 아이템별 다음 아이콘 이미지뷰 클릭 리스너 -> CourseSetActivity 로 화면 이동
            override fun click(position: Int) {
                val intent: Intent = Intent(this@CourseSelectActivity, CourseSetActivity::class.java)
                intent.putExtra("walkNumber", position)
                startActivity(intent)
            }
        })
        binding.courseSelectRv.adapter = adapter
    }

    private fun setMyEventListener() {
        binding.courseSelectCancelTv.setOnClickListener {
            finish()
        }
    }

    private fun observe() {
        vm.selfCourseEntities.observe(this, Observer {
            //프로그래스바 INVISIBLE
            binding.courseSelectLoadingPb.visibility = View.INVISIBLE

            //리사이클러뷰 어댑터에 전달할 데이터 만들기
            val items = mutableListOf<WalkVerCSVT>(WalkVerCSVT(WalkVerCSVT.TYPE_A))
            for (course in it) {
                items.add(WalkVerCSVT(WalkVerCSVT.TYPE_B, course))
            }

            //리사이블러뷰 어댑터에게 데이터 전달
            adapter.setData(items)
        })
    }
}